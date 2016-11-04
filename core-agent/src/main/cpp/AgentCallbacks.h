//
// Created by Jakub Háva on 08/04/16.
//

#ifndef DISTRACE_AGENT_CORE_AGENTCALLBACKS_H
#define DISTRACE_AGENT_CORE_AGENTCALLBACKS_H

#include <jni.h>
#include <jvmti.h>
#include <spdlog/logger.h>
#include "Agent.h"

namespace Distrace {

    /**
     * This class contains all JVMTI callbacks
     */
    class AgentCallbacks {
    public:
        /**
         * Callback we get when loading a new class
         * This callback is also responsible for instrumentation of desired classes
         *
         * It does so by sending the class bytes and class bytes of all dependencies to the instrumentor JVM which
         * provides the instrumentation if required and sends back the new byte code.
         */
        static void JNICALL cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *jni,
                                                jclass classBeingRedefined, jobject loader,
                                                const char *name, jobject protectionDomain,
                                                jint classDataLen, const unsigned char *class_data,
                                                jint *newClassDataLen, unsigned char **newClassData);

        /**
         * Callback we get when the JVM is initialized.
         */
        static void JNICALL callbackVMInit(jvmtiEnv *jvmti, JNIEnv *jni, jthread thread);

        /**
         * Callback we receive when the JVM terminates.
         * JNI/JVMTI functions can not be called after callback.
         */
        static void JNICALL callbackVMDeath(jvmtiEnv *jvmti, JNIEnv *jni);

        /**
         * Callback we receive when the JVM starts up, but before it's initialized.
         * It sets up the JNI calls.
         */
        static void JNICALL cbVMStart(jvmtiEnv *jvmti, JNIEnv *jni);

        /**
         * Callback we get when a class is loaded
         */
        static void JNICALL cbClassLoad(jvmtiEnv *jvmti, JNIEnv *jni, jthread thread, jclass clazz);

        /**
         * Callback we get when a when class is prepared
         */
        static void JNICALL cbClassPrepare(jvmtiEnv *jvmti, JNIEnv *jni, jthread thread, jclass clazz);


    };
}

#endif //DISTRACE_AGENT_CORE_AGENTCALLBACKS_H
