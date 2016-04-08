//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>
#include <cstring>
#include <iostream>
#include "Agent.h"
#include "AgentUtils.h"


using namespace DistraceAgent;

GlobalAgentData* Agent::globalData;
void Agent::init_global_data() {
    static GlobalAgentData data;
    data.jvmti = NULL;
    data.jvm = NULL;
    data.jni = NULL;
    data.vm_dead = (jboolean) false;
    data.vm_started = (jboolean) false;
    Agent::globalData = &data;

}
JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {
    Agent::init_global_data();
    Agent::globalData->jvm = vm;
    return AgentUtils::init_agent();
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    Agent::init_global_data();
    Agent::globalData->jvm = jvm;
    return AgentUtils::init_agent();
}
