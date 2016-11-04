//
// Created by Jakub Háva on 08/04/16.
//
#include "jvmti.h"
#include "../Agent.h"

#ifndef DISTRACE_AGENT_CORE_AGENTUTILS_H
#define DISTRACE_AGENT_CORE_AGENTUTILS_H

namespace Distrace {
    /**
     * This namespace contains various utilities methods to work with the native agent
     */
    namespace AgentUtils {

        const int JNI_ALREADY_ATTACHED = 1;
        const int JNI_ATTACHED_NOW = 2;

        /**
        * Enter a critical section by doing a JVMTI Raw Monitor Enter
        */
        void enterCriticalSection(jvmtiEnv *jvmti);

        /**
         * Exit a critical section by doing a JVMTI Raw Monitor Exit
         */
        void exitCriticalSection(jvmtiEnv *jvmti);

        /**
         * Check for error after a JVMTI method was called. If there was no error then content of ok_description
         * is logged out
         */
        int checkJVMTIError(jvmtiEnv *jvmti, jvmtiError errorNum, std::string okDescription,
                                   std::string errorDescription);

        /**
         * Check for error after a JVMTI method was called
         */
        int checkJVMTIError(jvmtiEnv *jvmti, jvmtiError errorNum, std::string errorDescription);

        /**
         * Register capabilities we need to have in the JVM
         */
        int registerCapabilities(jvmtiEnv *jvmti);

        /**
         * Register all necessary callbacks in the JVM
         */
        int registerCallbacks(jvmtiEnv *jvmti);

        /**
         * Register for events
         */
        int registerEvents(jvmtiEnv *jvmti);

        /**
         * Creates JVMTI environment with the desired JVMTI version
         */
        int createJVMTIEnv(JavaVM *jvm, jvmtiEnv *jvmti);

        /**
         * Creates JNI environment with the desired JNI version
         */
        int JNIAttachCurrentThread(JNIEnv *jni);

        /**
         * Dettach JNI from the current Java thread
         */
        void detachJNIFromCurrentThread(int attachStatus);

        /**
         * Attach JNI to the current Java thread
         */
        int attachJNItoCurrentThread(JavaVM *jvm, JNIEnv *jni);

        /**
         * Creates JVMTI lock
         */
        int createJVMTILock(jvmtiEnv *jvmti);

        /**
         * Initialize the agent
         */
        int initAgent();
    }
}

#endif //DISTRACE_AGENT_CORE_AGENTUTILS_H
