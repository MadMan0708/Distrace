//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>
#include "AgentCallbacks.h"
#include "utils/AgentUtils.h"
#include "utils/JavaUtils.h"
#include "bytecode/ClassParser.h"

using namespace Distrace;
using namespace Distrace::Logging;

void JNICALL AgentCallbacks::cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *jni,
                                                 jclass classBeingRedefined, jobject loader,
                                                 const char *name, jobject protectionDomain,
                                                 jint classDataLen, const unsigned char *classData,
                                                 jint *newClassDataLen, unsigned char **newClassData) {

    AgentUtils::enterCriticalSection(jvmti);
    // Do not handle classes which are being loaded before VM is started
    if (Agent::globalData->vmStarted) {
        int attachStatus = AgentUtils::JNIAttachCurrentThread(jni);
        auto loaderName = JavaUtils::getClassLoaderName(jni, loader);
        // parse the name since name passed to onClassFileLoadHook can be NULL
        auto className = ClassParser::parseJustName(classData, classDataLen);
        log(LOGGER_AGENT_CALLBACKS)->info("BEFORE LOADING: {} is about to be loaded by {}, is redefined = {} ", className, loaderName,
                                          classBeingRedefined != NULL);

        // Do not try to instrument classes loaded by ignored class loaders and auxiliary classes from byte buddy
        if (Agent::getInstApi()->shouldContinue(className, loaderName)) {

            if(!Agent::getInstApi()->isClassOnInstrumentor(className)){
                Agent::getInstApi()->sendClassData(className, classData, classDataLen);
                Agent::getInstApi()->loadDependencies(jni, className, loader, classData, classDataLen);
                Agent::getInstApi()->instrument(className, newClassData, newClassDataLen);
            }
        }

        log(LOGGER_AGENT_CALLBACKS)->info("AFTER LOADING: {} loaded by {}", className, loaderName);
        AgentUtils::detachJNIFromCurrentThread(attachStatus);
    }
    AgentUtils::exitCriticalSection(jvmti);
}


void JNICALL AgentCallbacks::callbackVMInit(jvmtiEnv *jvmti, JNIEnv *jni, jthread thread) {
    Agent::globalData->vmStarted = JNI_TRUE;
    log(LOGGER_AGENT_CALLBACKS)->info("The virtual machine has been initialized!");
}

void JNICALL AgentCallbacks::callbackVMDeath(jvmtiEnv *jvmti, JNIEnv *jni) {
    Agent::globalData->vmDead = JNI_TRUE;
    // stop the instrumentor JVM ( only in local mode )
    if (Agent::getArgs()->isRunningInLocalMode()) {
        Agent::globalData->instApi->stop();
    }

    log(LOGGER_AGENT_CALLBACKS)->info("The virtual machine has been terminated!");
}

void JNICALL AgentCallbacks::cbVMStart(jvmtiEnv *jvmti, JNIEnv *jni) {
    log(LOGGER_AGENT_CALLBACKS)->info("The virtual machine has been started!");
}

void JNICALL AgentCallbacks::cbClassLoad(jvmtiEnv *jvmti, JNIEnv *jni, jthread thread, jclass clazz) {
    //log(LOGGER_AGENT_CALLBACKS)->debug("Class: {} loaded", JavaUtils::getClassName(jni, clazz));
}

void JNICALL AgentCallbacks::cbClassPrepare(jvmtiEnv *jvmti, JNIEnv *jni, jthread thread, jclass klass) {
    //log(LOGGER_AGENT_CALLBACKS)->debug("Class: {} prepared", JavaUtils::getClassName(jni, klass));
}