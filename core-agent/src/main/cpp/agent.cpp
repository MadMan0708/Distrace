//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>

#include "Agent.h"
#include "AgentUtils.h"
#include "Utils.h"
#include <boost/algorithm/string.hpp>

using namespace Distrace;
using namespace Distrace::Logging;

// define global structures
GlobalAgentData *Agent::globalData;


void Agent::init_global_data() {
    static GlobalAgentData data;
    data.jvmti = NULL;
    data.jvm = NULL;
    data.vm_dead = (jboolean) false;
    data.vm_started = (jboolean) false;
    data.agent_args = new AgentArgs();
    Agent::globalData = &data;
}

AgentArgs *Agent::getArgs() {
    return Agent::globalData->agent_args;
}

void Agent::set_log_level_and_log() {
    auto args = Agent::getArgs();
    if (args->is_arg_set(AgentArgs::ARG_LOG_LEVEL)) {
        auto log_level = args->get_arg_value(AgentArgs::ARG_LOG_LEVEL);
        set_log_level(log_level);
        log(LOGGER_AGENT)->info() << "Log level successfully set to: " << log_level;
    }
}


JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {
    register_loggers();
    Agent::init_global_data();

    // parse the arguments before we do other work since other methods depends on valid arguments
    std::string err_msg;
    int res = Agent::globalData->agent_args->parse_args(options, err_msg);
    // set the log level after arguments are parsed since log level can be specified using the argument ARG_LOG_LEVEL
    Agent::set_log_level_and_log();
    log(LOGGER_AGENT)->info("Agent attached to the running JVM");

    // now print the result of parsing
    if (res == JNI_ERR) {
        log(LOGGER_AGENT)->error() << err_msg;
        // stop the agent in case arguments are wrong
        return JNI_ERR;
    } else {
        // print all parsed arguments
        for (auto pair : Agent::getArgs()->getArgsMap()) {
            log(LOGGER_AGENT)->info() << "Argument passed to the agent: " << pair.first << "=" << pair.second;
        }
    }

    log(LOGGER_AGENT)->error("Attaching to running JVM is not supported at this moment");

    Agent::globalData->jvm = vm;
    return JNI_ERR; //  return AgentUtils::init_agent();
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    Agent::init_global_data();

    // parse the arguments before we do other work since other methods depends on valid arguments
    std::string err_msg;
    int res = Agent::globalData->agent_args->parse_args(options, err_msg);

    register_loggers();

    // set the log level after arguments are parsed since log level can be specified using the argument ARG_LOG_LEVEL
    Agent::set_log_level_and_log();
    log(LOGGER_AGENT)->info("Agent started together with the start of the JVM");

    // now print the result of parsing
    if (res == JNI_ERR) {
        log(LOGGER_AGENT)->error() << err_msg;
        // stop the agent in case arguments are wrong
        return JNI_ERR;
    } else {
        // print all parsed arguments
        for (auto pair : Agent::getArgs()->getArgsMap()) {
            log(LOGGER_AGENT)->info() << "Argument passed to the agent: " << pair.first << "=" << pair.second;
        }
    }
    
    Agent::globalData->jvm = jvm;
    return AgentUtils::init_agent();
}
