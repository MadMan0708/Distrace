//
// Created by Jakub Háva on 08/04/16.
//
#include <jvmti.h>
#ifndef DISTRACE_AGENT_CORE_AGENT_H
#define DISTRACE_AGENT_CORE_AGENT_H

namespace DistraceAgent {

    typedef struct {
        /* JVMTI Environment */
        jvmtiEnv *jvmti = NULL;
        JNIEnv * jni = NULL;
        JavaVM* jvm = NULL;
        jboolean vm_started = (jboolean) false;
        jboolean vm_dead = (jboolean) false;
    } GlobalAgentData;

    class Agent {
    public:
        static GlobalAgentData *globalData;
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

        /*
         * Callback we get when the JVM starts up, but before its initialized.
         * Sets up the JNI calls.
         */
        static void JNICALL cbVMStart(jvmtiEnv *jvmti, JNIEnv *env);

        /*
         * Callback we receive when the JVM terminates - no more functions can be called after this
         */
        static void JNICALL callbackVMDeath(jvmtiEnv *jvmti_env, JNIEnv *jni_env);

        /*
         * Callback we get when the JVM is initialized.
         */
        static void JNICALL callbackVMInit(jvmtiEnv *jvmti, JNIEnv *env, jthread thread);

        /*
         * Callback when loading new class
         */
        static void JNICALL cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *env,
                                                jclass class_being_redefined, jobject loader,
                                                const char *name, jobject protection_domain,
                                                jint class_data_len, const unsigned char *class_data,
                                                jint *new_class_data_len, unsigned char **new_class_data);
    };
}

#endif //DISTRACE_AGENT_CORE_AGENT_H
