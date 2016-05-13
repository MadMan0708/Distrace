//
// Created by Jakub Háva on 08/04/16.
//
#include "jvmti.h"
#include "Agent.h"

#ifndef DISTRACE_AGENT_CORE_AGENTUTILS_H
#define DISTRACE_AGENT_CORE_AGENTUTILS_H

namespace DistraceAgent {

    static int JNI_ALREADY_ATTACHED = 1;
    static int JNI_ATTACHED_NOW = 2;
    class AgentUtils {
    public:
        static int check_jvmti_error(jvmtiEnv *env, jvmtiError error_number, std::string ok_description, std::string error_description);

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

        /*
         * Creates JVMTI environment with the desired JVMTI version
         */
        static int create_JVMTI_env(JavaVM *jvm, jvmtiEnv *jvmti);

        /*
        * Creates JNI environment with the desired JNI version
        */
        static int JNI_AttachCurrentThread(JNIEnv *jni);

        static void JNI_DettachCurrentThread(int attachStatus);
        /*
         * Initializes the agent
         */
        static int init_agent(std::string options);


        static jobject getInterceptorsClassLoader(JNIEnv* jni);

        static  std::shared_ptr<spdlog::logger> logger;

        static int attachJNIToCurrentThread(JavaVM* jvm, JNIEnv* jni);

    };
}


#endif //DISTRACE_AGENT_CORE_AGENTUTILS_H
