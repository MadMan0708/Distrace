//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>
#include "Agent.h"
#include "AgentUtils.h"


using namespace DistraceAgent;

GlobalAgentData* Agent::globalData;

JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {
    Agent::globalData->jvm = vm;
    return AgentUtils::init_agent(Agent::globalData);
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    Agent::globalData->jvm = jvm;
    return AgentUtils::init_agent(Agent::globalData);
}
