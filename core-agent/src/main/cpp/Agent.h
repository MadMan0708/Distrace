//
// Created by Jakub Háva on 08/04/16.
//
#include <jvmti.h>
#ifndef DISTRACE_AGENT_CORE_AGENT_H
#define DISTRACE_AGENT_CORE_AGENT_H

namespace DistraceAgent {

    class Agent {
    public:
        jvmtiEnv *jvmti = NULL;
        JNIEnv *jni = NULL;
        JavaVM *jvm = NULL;
        jboolean vm_started = (jboolean) false;
        jboolean vm_dead = (jboolean) false;
        /**
         * Register capabilities we need to have in the JVM
         */
        void register_jvmti_capabilities();
        /**
         * Register all necessary callbacks in the JVM
         */
        void register_jvmti_callbacks();

        /**
         * Register for events
         */
        void register_jvmti_events();
        /*
        * Callback that is notified when our agent is loaded. Registers for event
        * notifications.
        */
        JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved);

        /*
         * Creates JVMTI environment with the desired JVMTI version
         */
        int create_JVMTI_env();

        /*
       * Creates JNI environment with the desired JNI version
       */
        int create_JNI_env();
    };
}

#endif //DISTRACE_AGENT_CORE_AGENT_H
