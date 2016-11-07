//
// Created by Jakub Háva on 13/05/16.
//



#include <nnxx/nn.h>
#include <nnxx/message.h>
#include <jni.h>
#include <nnxx/pair.h>
#include <boost/filesystem.hpp>
#include <boost/algorithm/string.hpp>
#include "InstrumentorAPI.h"
#include "utils/Utils.h"
#include "Agent.h"
#include "utils/AgentUtils.h"
#include "utils/JavaUtils.h"
#include "bytecode/ClassParser.h"


using namespace Distrace;
using namespace Distrace::Logging;

byte InstrumentorAPI::REQ_TYPE_INSTRUMENT = 0;
byte InstrumentorAPI::REQ_TYPE_STOP = 1;
byte InstrumentorAPI::REQ_TYPE_CHECK_HAS_CLASS = 2;
byte InstrumentorAPI::REQ_TYPE_REGISTER_BYTECODE = 3;
std::string InstrumentorAPI::ACK_REQ_MSG = "ack_req_msg";
std::string InstrumentorAPI::ACK_REQ_INST_YES = "ack_req_int_yes";
std::string InstrumentorAPI::ACK_REQ_INST_NO = "ack_req_int_no";
std::string InstrumentorAPI::ACK_REQ_AUX_CLASSES = "auxiliary_types";
std::string InstrumentorAPI::ACK_REQ_CLASS_ON_INSTRUMENTOR = "yes";
std::mutex InstrumentorAPI::mtx; // mutex for critical section

std::vector<std::string> InstrumentorAPI::ignoredPackages = {
        "java/",
        "sun/"
};

std::vector<std::string> InstrumentorAPI::ignoredLoaders =  {
        // bootstrap classloader, it loads system classes which we usually don't want to instrument
        "@Bootstrap",
        "sun.reflect.DelegatingClassLoader"
        // classloader created because of mechanism called "inflatation", it is created synthetically and loads synthetic classes
        // and we do not want to create type Descriptions for these internal java classes
        // see http://stackoverflow.com/questions/6505274/what-for-sun-jvm-creates-instances-of-sun-reflect-delegatingclassloader-at-runti
};

InstrumentorAPI::InstrumentorAPI(nnxx::socket socket) {
    this->socket = std::move(socket);
    this->pathToDirWithAuxClasses = Utils::createUniqueTempDir();
    log(LOGGER_INSTRUMENTOR_API)->info("Adding directory for auxiliary classes to classpath : {}", this->pathToDirWithAuxClasses);
    Agent::globalData->jvmti->AddToSystemClassLoaderSearch(this->pathToDirWithAuxClasses.c_str());
}

void InstrumentorAPI::loadDependencies(JNIEnv *jni, std::string className, jobject loader, const unsigned char *classData,
                                       jint classDataLen) {

    log(LOGGER_BYTECODE)->info("Parsing: {}", className);
    ClassParser *parser = ClassParser::parse(classData, classDataLen);
    auto allRefs = parser->getAllRefs();
    for (auto &ref : allRefs) {
        log(LOGGER_BYTECODE)->info("Found -> {}", ref);
        if (!isInIgnoredPackage(ref)) {
            const unsigned char *classBytes;
            if(!Agent::getInstApi()->isClassOnInstrumentor(ref)){
                int classBytesLen = sendReferencedClass(jni, ref, loader, &classBytes);

                // skip the case when class data couldn't be obtained using the input stream method
                if (classBytesLen != 0){
                    // if class bytes for the previous class hasn't been sent, proceed with recursion
                    loadDependencies(jni, ref.c_str(), loader, classBytes, classBytesLen);
                }
            }
        }
    }
    delete parser;
}

void InstrumentorAPI::instrument(std::string className, unsigned char **newClassData, jint *newClassDataLen) {
    log(LOGGER_INSTRUMENTOR_API)->info("About to instrument class: {}", className);
    // send instrumentor just name because it already has the class
    if (shouldInstrument(className)) {

        // receive length of the byte code
        auto lengthAsString = receiveStringReply();
        auto expectedLength = std::atoi(lengthAsString.c_str());

        // receive the new bytecode
        *newClassData = (byte *) malloc(sizeof(byte) *expectedLength);
        *newClassDataLen = receiveByteArrayReply(newClassData, expectedLength);

        log(LOGGER_INSTRUMENTOR_API)->info("The class {} has been instrumented.", className);
    }
}

bool InstrumentorAPI::shouldInstrument(std::string className) {
    // critical section. Communication started from different threads would break nanomsg
    mtx.lock();
    log(LOGGER_INSTRUMENTOR_API)->info("Asking Instrumentor whether it needs to instrument class {}", className);
    sendRequestAndAssertReply(REQ_TYPE_INSTRUMENT);

    // send class name
    sendStringRequest(className);
    bool returnVal = false;
    // receive auxiliary classes
    loadAuxiliaryClasses(className);
    auto reply = receiveStringReply();
    if (reply == ACK_REQ_INST_YES) {
        log(LOGGER_INSTRUMENTOR_API)->info("Instrumentor reply: Class {} will be instrumented.", className);
        returnVal = true;
    } else if (reply == ACK_REQ_INST_NO) {
        log(LOGGER_INSTRUMENTOR_API)->info("Instrumentor reply: Class {} will not be instrumented.", className);
    } else {
        throw std::runtime_error("Got unexpected reply in should_instrument method :" + reply);
    }
    mtx.unlock();
    return returnVal;
}

void InstrumentorAPI::loadAuxiliaryClasses(std::string className){
    log(LOGGER_INSTRUMENTOR_API)->info("Loading auxiliary classes for {}", className);
    auto reply = receiveStringReply();

    // keep loading auxiliary classes until we receive signal that there are any left
    while(reply == ACK_REQ_AUX_CLASSES){
        auto auxClassName = receiveStringReply();
        auto lengthAsString = receiveStringReply();
        auto expectedLength = std::atoi(lengthAsString.c_str());

        byte* classBytes = (byte *) malloc(sizeof(byte) * expectedLength);
        receiveByteArrayReply(&classBytes, expectedLength);
        log(LOGGER_INSTRUMENTOR_API)->info("Receive bytecode for auxiliary class {}", auxClassName);


        std::vector<std::string> tokens;
        boost::split(tokens, auxClassName, boost::is_any_of("."), boost::token_compress_on);
        auto fullName = tokens.back() + ".class";
        tokens.pop_back();

        // create path out of packages hierarchy
        std::string sep(1, boost::filesystem::path::preferred_separator);
        auto class_path = boost::algorithm::join(tokens, sep);
        auto path = pathToDirWithAuxClasses + class_path + sep;
        auto fullPath = path + fullName;

        // create directory structure same as the packages hierarchy in which is this class located
        boost::filesystem::create_directories(path);
        FILE* file = fopen(fullPath.c_str(), "wb" );
        if(file!=NULL){
            log(LOGGER_INSTRUMENTOR_API)->error("Writing to file {}", fullPath);

            fwrite(classBytes, sizeof(classBytes[0]), expectedLength, file);
            fclose(file);
        }else{
            log(LOGGER_INSTRUMENTOR_API)->error("Error opening the file {}", fullPath);
        }

        putToAuxClassCache(auxClassName);
        reply = receiveStringReply();
    }
}

int InstrumentorAPI::init() {
    const std::string connectionStr = Agent::getArgs()->getArgValue(AgentArgs::ARG_CONNECTION_STR);

    // launch Instrumentor JVM only in case of ipc, when tcp is set, the instrumentor JVM should be already running.
    if(Agent::getArgs()->isRunningInLocalMode()){
        // fork instrumentor JVM
        if (!system(NULL)) {
            log(LOGGER_INSTRUMENTOR_API)->error("Can't fork Instrumentor JVM, shell not available!");
            return JNI_ERR;
        }
        const std::string instrumentorServerJar = Agent::getArgs()->getArgValue(
                AgentArgs::ARG_INSTRUMENTOR_SERVER_JAR);
        const std::string instrumentorMainClass = Agent::getArgs()->getArgValue(
                AgentArgs::ARG_INSTRUMENTOR_MAIN_CLASS);
        const std::string instrumentorServerCP = Agent::getArgs()->getArgValue(AgentArgs::ARG_INSTRUMENTOR_SERVER_CP);
        const std::string logLevel = Agent::getArgs()->getArgValue(AgentArgs::ARG_LOG_LEVEL);
        const std::string logDir = Agent::getArgs()->getArgValue(AgentArgs::ARG_LOG_DIR);

        std::string launchCommand =
                "java -cp " + instrumentorServerJar + ":" + instrumentorServerCP + " " + instrumentorMainClass + " " + connectionStr + " " +
                logLevel + " " + logDir  + " & ";
        log(LOGGER_INSTRUMENTOR_API)->info("Starting Instrumentor JVM with the command: {}", launchCommand);
        int result = system(Utils::stringToCharPointer(launchCommand));
        if (result < 0) {
            log(LOGGER_INSTRUMENTOR_API)->error("Instrumentor JVM couldn't be forked because of error: {}", strerror(errno));
            return JNI_ERR;
        }
    }else{
        //TODO: check the connection
    }
    // create socket which is used to connect to the Instrumentor JVM
    nnxx::socket socket{nnxx::SP, nnxx::PAIR};

    int endpoint = socket.connect(connectionStr);
    if (endpoint < 0) {
        log(LOGGER_INSTRUMENTOR_API)->error("Returned error: {}. Connection to the instrumentor JVM can't be established! Is instrumentor JVM running ?",
                                           strerror(errno));
        return JNI_ERR;
    } else {
        log(LOGGER_INSTRUMENTOR_API)->info("Connection to the instrumentor JVM established. Assigned endpoint ID is = {}", endpoint);
    }

    Agent::globalData->instApi = new InstrumentorAPI(std::move(socket));

    // add instrumentor libraries jar on the classpath so our jvm can see created interceptors
    const std::string instrumentorLibJar = Agent::getArgs()->getArgValue(AgentArgs::ARG_INSTRUMENTOR_LIB_JAR);
    jvmtiError error = Agent::globalData->jvmti->AddToSystemClassLoaderSearch(instrumentorLibJar.c_str());

    auto ret = AgentUtils::checkJVMTIError(Agent::globalData->jvmti, error,
                                           "Path: " + instrumentorLibJar +
                                           " successfully added on the system's classloader search path",
                                           "Cannot add path " + instrumentorLibJar +
                                           " on the system's classloader search path");
    if(ret == JNI_ERR){
        return JNI_ERR;
    }

    error = Agent::globalData->jvmti->AddToBootstrapClassLoaderSearch(instrumentorLibJar.c_str());
    return AgentUtils::checkJVMTIError(Agent::globalData->jvmti, error,
                                       "Path: " + instrumentorLibJar +
                                       " successfully added on the bootstrap's classloader search path",
                                       "Cannot add path " + instrumentorLibJar +
                                       " on the bootstraps's classloader search path");
}

void InstrumentorAPI::stop() {
    // in case of local mode ( IPC communication) delete the file used for the communication
    log(LOGGER_INSTRUMENTOR_API)->info("Stopping the instrumentor JVM");
    // remove ipc://, the remaining part represents file ( when running on linux )
    std::string file = Agent::getArgs()->getArgValue(AgentArgs::ARG_CONNECTION_STR).substr(6);
    boost::filesystem::path fileToDelete(file);
    boost::filesystem::remove(fileToDelete);

    // remove also all auxiliary classes
    boost::filesystem::remove_all(pathToDirWithAuxClasses);

    // inform instrumentor JVM that the application is being stopped
    sendRequestAndAssertReply(REQ_TYPE_STOP);
}

bool InstrumentorAPI::shouldContinue(std::string className, std::string loaderName) {
    return !(isIgnoredClassLoader(loaderName) || isInAuxClassCache(className));
}

int InstrumentorAPI::sendClassData(std::string className, const unsigned char *classData, int classDataLen){
    log(LOGGER_INSTRUMENTOR_API)->info("Sending original bytecode to the instrumentor: {}", className);
    // send bytecode for current class
    mtx.lock();
    sendRequestAndAssertReply(REQ_TYPE_REGISTER_BYTECODE);
    sendStringRequest(className);
    sendByteArrayRequest(classData, classDataLen);
    mtx.unlock();
    Agent::globalData->instApi->putToInstrumentorClassesCache(className);
    return classDataLen;
}

int InstrumentorAPI::sendReferencedClass(JNIEnv *jni, std::string className, jobject loader, const unsigned char **classData){
    log(LOGGER_BYTECODE)->debug("Finding bytes for: {}", className);
    // find the class bytes using the specified class loader
    int classDataLen = JavaUtils::getBytesForClass(jni, className, loader, classData);
    if(classDataLen == 0){
        log(LOGGER_BYTECODE)->info("Class: {} wasn't found using the input stream method", className);
        JavaUtils::triggerLoadingWithSpecificLoader(jni, className, loader);
        return 0;
    }else{
        return sendClassData(className, *classData, classDataLen);
    }
}

bool InstrumentorAPI::isClassOnInstrumentor(std::string className){
    if(isInInstrumentorClassesCache(className)){
        return true;
    }else{
        mtx.lock();
        sendRequestAndAssertReply(REQ_TYPE_CHECK_HAS_CLASS);
        auto ret = sendAndReceive(className);
        mtx.unlock();

        if(ret == ACK_REQ_CLASS_ON_INSTRUMENTOR){
            // store the information about the class locally so we can minimize number of requests to the instrumentor
            Agent::globalData->instApi->putToInstrumentorClassesCache(className);
        }
        return  ret == ACK_REQ_CLASS_ON_INSTRUMENTOR;
    }
}

bool InstrumentorAPI::isInInstrumentorClassesCache(std::string className){
    std::string withSlashes = JavaUtils::toNameWithSlashes(className);
    log(LOGGER_INSTRUMENTOR_API)->info("Checking if class: {} is in the cache of classes on the instrumentor", withSlashes);
    return std::find(instrumentorClassesCache.begin(), instrumentorClassesCache.end(), withSlashes) != instrumentorClassesCache.end();
}

void InstrumentorAPI::putToInstrumentorClassesCache(std::string className){
    log(LOGGER_INSTRUMENTOR_API)->info("Class {} is on the instrumentor, adding the class name to the local cache "
                                               "of processed classes", className);
    instrumentorClassesCache.insert(className);
}

bool InstrumentorAPI::isInAuxClassCache(std::string className){

    std::string class_name(className);
    std::replace(class_name.begin(), class_name.end(), '/', '.');
    return std::find(auxClassesCache.begin(), auxClassesCache.end(), class_name) != auxClassesCache.end();
}

void InstrumentorAPI::putToAuxClassCache(std::string className){
    auxClassesCache.insert(className);
}

bool InstrumentorAPI::isInIgnoredPackage(std::string className) {
    for(std::vector<std::string>::iterator it = ignoredPackages.begin(); it != ignoredPackages.end(); ++it) {
        if(boost::starts_with(className, *it)){
            return true;
        }
    }
    return false;
}

bool InstrumentorAPI::isIgnoredClassLoader(std::string classLoader) {
    return std::find(ignoredLoaders.begin(), ignoredLoaders.end(), classLoader) != ignoredLoaders.end();
}


int InstrumentorAPI::sendStringRequest(std::string data) {
    auto originalLen = data.length();
    int numBytesSent = socket.send<std::string>(data);
    assertBytesSent(numBytesSent, originalLen);
    return numBytesSent;
}

std::string InstrumentorAPI::receiveStringReply() {
    // wait for reply
    return socket.recv<std::string>(0);
}

std::string InstrumentorAPI::sendAndReceive(std::string data) {
    sendStringRequest(data);
    return receiveStringReply();
}

int InstrumentorAPI::sendByteRequest(byte data) {
    byte *buf = (byte *) nn_allocmsg(1, 0);
    buf[0] = data;
    int numBytesSent = socket.send((void *) buf, 1, 0);
    assertBytesSent(numBytesSent, 1);
    return numBytesSent;
}

std::string InstrumentorAPI::sendAndReceive(byte data) {
    sendByteRequest(data);
    return receiveStringReply();
}

int InstrumentorAPI::sendByteArrayRequest(const byte *inputData, int inputDataLen) {
    auto numBytesSent = socket.send(inputData, inputDataLen, 0);
    assertBytesSent(numBytesSent, inputDataLen);
    return numBytesSent;
}

int InstrumentorAPI::receiveByteArrayReply(byte **inputBuffer, int expectedLength) {
    return socket.recv(*inputBuffer, expectedLength, 0);
}

int InstrumentorAPI::sendAndReceive(const byte *inputData, int inputDataLen, byte **outputBuffer) {
    sendByteArrayRequest(inputData, inputDataLen);
    auto lengthAsString = receiveStringReply();
    auto expectedLength = std::atoi(lengthAsString.c_str());
    *outputBuffer = (byte *) malloc(sizeof(byte) * expectedLength);
    return receiveByteArrayReply(outputBuffer, expectedLength);
}


void InstrumentorAPI::sendRequestAndAssertReply(byte requestType) {
    auto reply = sendAndReceive(requestType);
    assert(reply == ACK_REQ_MSG);
}

void InstrumentorAPI::assertBytesSent(int numBytesSent, size_t originalLen) {
    if (numBytesSent < 0) {
        log(LOGGER_INSTRUMENTOR_API)->error("Bytes couldn't be send, error: {}", strerror(errno));
    }
    assert(numBytesSent == originalLen);
}



