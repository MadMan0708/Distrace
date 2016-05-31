//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>

#include "Agent.h"
#include "AgentUtils.h"
#include "Logging.h"
#include "Utils.h"
#include <boost/algorithm/string.hpp>
#include <nnxx/reqrep.h>

using namespace Distrace;
using namespace Distrace::Logging;

// define argument names
const std::string Agent::ARG_INSTRUMENTOR_JAR = "instrumentorJar";
const std::string Agent::ARG_LOG_LEVEL = "log_level";
const std::string Agent::ARG_SOCKET_ADDRESS = "sock_address";


// define global structures
GlobalAgentData* Agent::globalData;


void Agent::init_global_data() {
    static GlobalAgentData data;
    data.jvmti = NULL;
    data.jvm = NULL;
    data.vm_dead = (jboolean) false;
    data.vm_started = (jboolean) false;
    Agent::globalData = &data;
}

std::string Agent::get_arg_value(std::string arg_name){
    return globalData->agent_args.find(arg_name)->second;
}

bool Agent::is_arg_set(std::string arg_name){
    return globalData->agent_args.find(arg_name) ==  globalData->agent_args.end();
}

void Agent::set_log_level_and_warn(std::map<std::string, std::string> &args){
    if(args.find(Agent::ARG_LOG_LEVEL) !=args.end()){
        if(!setLogLevel(args.find(Agent::ARG_LOG_LEVEL)->second)){
            log(LOGGER_AGENT)->error() << "Log level contains unknown value, using default log level!";
        }
    }
}

int Agent::check_for_mandatory_args(std::map<std::string, std::string> &args){
    if(args.find(ARG_INSTRUMENTOR_JAR) == args.end()){
        log(LOGGER_AGENT)->error() << "Mandatory argument \""<< ARG_INSTRUMENTOR_JAR<< "=<path>\" missing, stopping the agent!";
        return JNI_ERR;
    }

    return JNI_OK;
}

int Agent::parse_args(std::string options, std::map<std::string, std::string> &args){
    // first split to arguments pairs
    std::vector<std::string> pairs;
    boost::split(pairs, options, boost::is_any_of(";"),boost::token_compress_on);
    for(int i=0; i<pairs.size(); i++) {
        // Skip the empty pairs. For example, empty string is added to pairs vector of options ends with ;
        if (!pairs[i].empty()) {
            std::vector<std::string> arg_split;
            boost::split(arg_split, pairs[i], boost::is_any_of("="));
            if (arg_split.size() != 2) {
                Agent::set_log_level_and_warn(args);
                // it means the argument line does not match the pattern name1=value1;name2=value2
                log(LOGGER_AGENT)->error() << "Wrong argument pair:" << pairs[i] << ", arguments have to have format name=value";
                return JNI_ERR;
            } else {
                auto previous = args.insert({arg_split[0], arg_split[1]});
                if (!previous.second) {
                    Agent::set_log_level_and_warn(args);
                    log(LOGGER_AGENT)->error() << "Argument " << arg_split[0] << " is already defined. Arguments can be defined only once!";
                    return JNI_ERR;
                }
            }
        }
    }

    Agent::set_log_level_and_warn(args);

    for( auto pair : args){
        log(LOGGER_AGENT)->info() << "Argument passed to the agent: "<< pair.first << "=" << pair.second;
    }

    if(Agent::check_for_mandatory_args(args)==JNI_ERR){
        return JNI_ERR;
    }
    Agent::globalData->agent_args = args;
    return JNI_OK;
}

JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {
    registerLoggers();
    std::map<std::string, std::string> args; // key = arg name, value = arg value
    if(Agent::parse_args(options, args) == JNI_ERR){
        // stop the agent in case arguments are wrong
        return JNI_ERR;
    }
    log(LOGGER_AGENT)->info("Agent attached to the running JVM");
    log(LOGGER_AGENT)->error("Attaching to running JVM is not supported at this moment");

    Agent::init_global_data();
    Agent::globalData->jvm = vm;
    return JNI_ERR; //  return AgentUtils::init_agent();
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    registerLoggers();
    std::map<std::string, std::string> args; // key = arg name, value = arg value
    if(Agent::parse_args(options, args) == JNI_ERR){
        // stop the agent in case arguments are wrong
        return JNI_ERR;
    }
    log(LOGGER_AGENT)->info("Agent started together with the start of the JVM");

    Agent::init_global_data();
    Agent::globalData->jvm = jvm;
    return AgentUtils::init_agent();
}
