//
// Created by Jakub Háva on 08/04/16.
//
#include "jvmti.h"
#include "Agent.h"

#ifndef DISTRACE_AGENT_CORE_AGENTUTILS_H
#define DISTRACE_AGENT_CORE_AGENTUTILS_H

namespace DistraceAgent {
    class AgentUtils {
    public:
        static int check_jvmti_error(jvmtiEnv *env, jvmtiError error_number, const char *error_description);
        /**
        * Register capabilities we need to have in the JVM
        */
        static void register_jvmti_capabilities(jvmtiEnv *jvmti);

        /**
         * Register all necessary callbacks in the JVM
         */
        static void register_jvmti_callbacks(jvmtiEnv *jvmti);

        /**
         * Register for events
         */
        static void register_jvmti_events(jvmtiEnv *jvmti);

        /*
         * Creates JVMTI environment with the desired JVMTI version
         */
        static int create_JVMTI_env(JavaVM *jvm, jvmtiEnv *jvmti);

        /*
        * Creates JNI environment with the desired JNI version
        */
        static int create_JNI_env(JavaVM *jvm, JNIEnv *jni);

        /*
         * Initializes the agent
         */
        static int init_agent(std::string options);
    };
}


#endif //DISTRACE_AGENT_CORE_AGENTUTILS_H
