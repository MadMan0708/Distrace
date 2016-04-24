//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>
#include <spdlog/logger.h>
#include "Agent.h"
#include "AgentUtils.h"
#include "Logger.h"
#include "Utils.h"
#include <boost/algorithm/string.hpp>
#include <dlfcn.h>

#define PATH_SEPARATOR ':' /* define it to be ':' on Solaris */
#define USER_CLASSPATH "." /* where Prog.class is */

using namespace DistraceAgent;

// define argument names
const std::string Agent::ARG_INSTRUMENTOR_JAR = "instrumentorJar";
const std::string Agent::ARG_JVM_LIB = "jvmLib";

// define global structures
GlobalAgentData* Agent::globalData;
std::shared_ptr<spdlog::logger> logger = Logger::getLogger("Agent");

void Agent::init_global_data() {
    static GlobalAgentData data;
    data.jvmti = NULL;
    data.jvm = NULL;
    data.jni = NULL;
    data.vm_dead = (jboolean) false;
    data.vm_started = (jboolean) false;
    Agent::globalData = &data;
}

int Agent::parse_args(std::string options, std::map<std::string, std::string> *args){
    // first split to arguments pairs
    std::vector<std::string> pairs;
    boost::split(pairs, options,boost::is_any_of(";"),boost::token_compress_on);

    for(int i=0; i<pairs.size(); i++) {
        // Skip the empty pairs. For example, empty string is added to pairs vector of options ends with ;
        if (!pairs[i].empty()) {
            std::vector<std::string> arg_split;
            boost::split(arg_split, pairs[i], boost::is_any_of("="));
            if (arg_split.size() != 2) {
                // it means the argument line does not match the pattern name1=value1;name2=value2
                logger->error() << "Wrong argument pair:" << pairs[i] << ", arguments have to have format name=value";
                return JNI_ERR;
            } else {
                auto previous = args->insert({arg_split[0], arg_split[1]});
                if (!previous.second) {
                    logger->error() << "Argument " << arg_split[0] << " is already defined. Arguments can be defined only once!";
                    return JNI_ERR;
                }
            }
        }
    }

    for( auto pair : *args){
        logger->info() << "Argument passed to the agent: "<< pair.first << "=" << pair.second;
    }

    // check for mandatory arguments
    if(args->find(ARG_INSTRUMENTOR_JAR) == args->end()){
        logger->error() << "Mandatory argument \""<< ARG_INSTRUMENTOR_JAR<< "=<path>\" missing, stopping the agent!";
        return JNI_ERR;
    }
    if(args->find(ARG_JVM_LIB) == args->end()){
        logger->error() << "Mandatory argument \""<< ARG_JVM_LIB<< "=<path>\" missing, stopping the agent!";
        return JNI_ERR;
    }
    return JNI_OK;
}

int Agent::init_intrumenter(std::string path_to_jar, std::string jvm_lib){
    JavaVM *jvm;       /* denotes a Java VM */
    JNIEnv *env;       /* pointer to native method interface */
    JavaVMInitArgs vm_args; /* JDK/JRE 6 VM initialization arguments */
    JavaVMOption options[1];
    options[0].optionString = Utils::stringToCharPointer("-Djava.class.path="+path_to_jar);
    vm_args.version = JNI_VERSION_1_6;
    vm_args.nOptions = 1;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = JNI_FALSE;
    /* load and initialize a Java VM, return a JNI interface
     * pointer in env */

    jsize nVMs;
    JNI_GetCreatedJavaVMs(NULL, 0, &nVMs); // 1. just get the required array length
    JavaVM** buffer = new JavaVM*[nVMs];
    JNI_GetCreatedJavaVMs(buffer, nVMs, &nVMs); // 2. get the data

    logger->info() << "JVM existing" << nVMs;
    typedef _JNI_IMPORT_OR_EXPORT_ jint (JNICALL *JNI_CreateJavaVM_func)(JavaVM **pvm, void **penv, void *args);

    auto jvm_lib_ref = dlopen(jvm_lib.c_str(), RTLD_LAZY);
    if(jvm_lib_ref == NULL){
        logger->error() << "JVM Library not loaded successfully, the path to it specified:  "<< jvm_lib;
        return JNI_ERR;
    }
    logger->info() << "JVM Library loaded successfully. ";

    JNI_CreateJavaVM_func JNI_CreateJavaVM_ptr = (JNI_CreateJavaVM_func)dlsym(jvm_lib_ref, "JNI_CreateJavaVM");
    if(JNI_CreateJavaVM_ptr == NULL){
        logger->error() << "Method JNI_CreateJavaVM wasn't bound successfully, jvm library used: "<< jvm_lib;
        return JNI_ERR;
    }
    logger->info() << "Method JNI_CreateJavaVM was bound successfully!";

    int ret = JNI_CreateJavaVM_ptr(&jvm, (void **)&env, &vm_args);
    if(ret != JNI_OK){
        logger->error() << "Instrumentor JVM could not be initialised (using JNI). JNI_CreateJavaVM ERROR: " << ret;
        return JNI_ERR;
    }
    /* invoke the Instrumentor.main method using the JNI */

   // jclass cls = env->FindClass("com/distrace/Instrumentor");
    //jmethodID mid = env->GetStaticMethodID(cls, "main", "I");
    //env->CallStaticVoidMethod(cls, mid, 100);
    /* We are done. */
    //jvm->DestroyJavaVM();
    return JNI_OK;
}

JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {
    logger->info("Attaching to running JVM");

    Agent::init_global_data();
    Agent::globalData->jvm = vm;
    return AgentUtils::init_agent();
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    logger->info("Agent started together with the start of the JVM");

    std::map<std::string, std::string> args; // key = arg name, value = arg value
    if(Agent::parse_args(options, &args) == JNI_ERR){
        // stop the agent in case arguments are wrong
        return JNI_ERR;
    }
    if(Agent::init_intrumenter(args.find(Agent::ARG_INSTRUMENTOR_JAR)->second, args.find(Agent::ARG_JVM_LIB)->second) == JNI_ERR){
        // stop the agent in case instrumenter could not be started
        return JNI_ERR;
    }
    Agent::init_global_data();
    Agent::globalData->jvm = jvm;
    return AgentUtils::init_agent();
}
