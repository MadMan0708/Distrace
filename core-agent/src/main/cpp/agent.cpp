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
#include <nnxx/reqrep.h>

using namespace DistraceAgent;

// define argument names
const std::string Agent::ARG_INSTRUMENTOR_JAR = "instrumentorJar";

// define global structures
std::vector<std::string> Agent::internal_classes_to_instrument = Agent::init_list_of_classes_to_instrument();
GlobalAgentData* Agent::globalData;
std::shared_ptr<spdlog::logger> logger = Logger::getLogger("Agent");

std::vector<std::string> Agent::init_list_of_classes_to_instrument(){
    std::vector<std::string> classes = {
            // here declare list of all classes which needs to be instrumented and we know about them in advance
            "java/lang/Object"
    };
    std::sort(classes.begin(),classes.end());
    return classes;
}

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
    boost::split(pairs, options, boost::is_any_of(";"),boost::token_compress_on);

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

    if(args->find(ARG_INSTRUMENTOR_JAR) == args->end()){
        logger->error() << "Mandatory argument \""<< ARG_INSTRUMENTOR_JAR<< "=<path>\" missing, stopping the agent!";
        return JNI_ERR;
    }
    return JNI_OK;
}

int Agent::init_instrumenter(std::string path_to_jar) {
    if(!system(NULL)){
        logger->error() << "Could not fork instrumentor JVM";
        return JNI_ERR;
    }
    // fork instrumentor JVM
    system(Utils::stringToCharPointer("java -jar "+path_to_jar + " & "));

    nnxx::socket socket{nnxx::SP, nnxx::REQ};
    const std::string addr = "ipc://test";
    int endpoint = socket.connect(addr);
    if(endpoint < 0){
        logger->error() << "Returned error code "<< errno << ". Connection to the instrumentor JVM can't be established! Is instrumentor JVM running ?";
        return JNI_ERR;
    }else{
        logger->info() << "Connection to the instrumentor JVM established via IPC. Assigned endpoint ID is "<< endpoint;
    }
    Agent::globalData->inst_socket = std::move(socket);
    return JNI_OK;
}

JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {
    logger->error("Attaching to running JVM is not supported at this moment");

   /* logger->info("Attaching to running JVM");
    Agent::init_global_data();
    Agent::globalData->jvm = vm;
    return AgentUtils::init_agent(options);*/
    
    return JNI_ERR;
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    logger->info("Agent started together with the start of the JVM");

    Agent::init_global_data();
    Agent::globalData->jvm = jvm;
    return AgentUtils::init_agent(options);
}
