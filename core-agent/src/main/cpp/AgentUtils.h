//
// Created by Jakub Háva on 08/04/16.
//
#include "jvmti.h"
#include "Agent.h"

#ifndef DISTRACE_AGENT_CORE_AGENTUTILS_H
#define DISTRACE_AGENT_CORE_AGENTUTILS_H

namespace Distrace {

    static int JNI_ALREADY_ATTACHED = 1;
    static int JNI_ATTACHED_NOW = 2;

    class AgentUtils {
    public:

        /**
         * Check for error after a JVMTI method was called. If there was no error then content of ok_description
         * is logged out
         */
        static int check_jvmti_error(jvmtiEnv *env, jvmtiError error_number, std::string ok_description,
                                     std::string error_description);

        /**
         * Check for error after a JVMTI method was called
         */
        static int check_jvmti_error(jvmtiEnv *env, jvmtiError error_number, std::string error_description);

        /**
         * Register capabilities we need to have in the JVM
         */
        static int register_jvmti_capabilities(jvmtiEnv *jvmti);

        /**
         * Register all necessary callbacks in the JVM
         */
        static int register_jvmti_callbacks(jvmtiEnv *jvmti);

        /**
         * Register for events
         */
        static int register_jvmti_events(jvmtiEnv *jvmti);

        /**
         * Creates JVMTI environment with the desired JVMTI version
         */
        static int create_JVMTI_env(JavaVM *jvm, jvmtiEnv *jvmti);

        /**
         * Creates JNI environment with the desired JNI version
         */
        static int JNI_AttachCurrentThread(JNIEnv *jni);

        /**
         * Initialize the agent
         */
        static int init_agent();

        /**
         * Dettach JNI from the current Java thread
         */
        static void dettach_JNI_from_current_thread(int attachStatus);

        /**
         * Attach JNI to the current Java thread
         */
        static int attach_JNI_to_current_thread(JavaVM *jvm, JNIEnv *jni);

    };
}


#endif //DISTRACE_AGENT_CORE_AGENTUTILS_H
