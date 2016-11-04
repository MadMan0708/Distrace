//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>

#include "Agent.h"
#include "utils/AgentUtils.h"
#include "utils/Utils.h"
#include <boost/algorithm/string.hpp>

using namespace Distrace;
using namespace Distrace::Logging;

// define global structures
GlobalAgentData *Agent::globalData;


void Agent::initGlobalData() {
    static GlobalAgentData data;
    data.jvmti = NULL;
    data.jvm = NULL;
    data.vmDead = (jboolean) false;
    data.vmStarted = (jboolean) false;
    data.args = new AgentArgs();
    Agent::globalData = &data;
}

InstrumentorAPI *Agent::getInstApi() {
    return Agent::globalData->instApi;
}

AgentArgs *Agent::getArgs() {
    return Agent::globalData->args;
}

JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {
    Agent::initGlobalData();

    // parse the arguments before we do other work since other methods depends on valid arguments
    std::string err_msg;
    int res = Agent::globalData->args->parse_args(options, err_msg);

    // loggers has to be registered after the arguments has been parsed
    register_loggers();
    log(LOGGER_AGENT)->info("Agent attached to the running JVM");

    // now print the result of parsing
    if (res == JNI_ERR) {
        log(LOGGER_AGENT)->error("{}", err_msg);
        // stop the agent in case arguments are wrong
        return JNI_ERR;
    } else {
        // print all parsed arguments
        for (auto pair : Agent::getArgs()->getArgsMap()) {
            log(LOGGER_AGENT)->info("Argument passed to the agent: {} = {}", pair.first, pair.second);
        }
    }

    log(LOGGER_AGENT)->error("Attaching to running JVM is not supported at this moment");

    Agent::globalData->jvm = vm;
    return JNI_ERR; //  return AgentUtils::init_agent();
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    Agent::initGlobalData();

    // parse the arguments before we do other work since other methods depends on valid arguments
    std::string err_msg;
    int res = Agent::globalData->args->parse_args(options, err_msg);

    // loggers has to be registered after the arguments has been parsed
    register_loggers();
    log(LOGGER_AGENT)->info("Agent started together with the start of the JVM");

    // now print the result of parsing
    if (res == JNI_ERR) {
        log(LOGGER_AGENT)->error("{}", err_msg);
        // stop the agent in case arguments are wrong
        return JNI_ERR;
    } else {
        // print all parsed arguments
        for (auto pair : Agent::getArgs()->getArgsMap()) {
            log(LOGGER_AGENT)->info("Argument passed to the agent: {} = {}", pair.first, pair.second);
        }
    }
    
    Agent::globalData->jvm = jvm;
    return AgentUtils::initAgent();
}
