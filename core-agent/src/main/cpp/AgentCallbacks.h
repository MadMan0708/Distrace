//
// Created by Jakub Háva on 08/04/16.
//

#ifndef DISTRACE_AGENT_CORE_AGENTCALLBACKS_H
#define DISTRACE_AGENT_CORE_AGENTCALLBACKS_H

#include <jni.h>
#include <jvmti.h>
#include "Agent.h"

namespace DistraceAgent {

class AgentCallbacks {
    public:
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


    static void JNICALL cbClassLoad(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jclass klass);

    static void JNICALL cbClassPrepare(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jclass klass);
};
}

#endif //DISTRACE_AGENT_CORE_AGENTCALLBACKS_H
