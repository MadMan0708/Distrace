/*
 * Main code of Agent library. This code is written in C++11
 */
#include <stdio.h>
#include <string.h>
#include <iostream>
#include "jvmti.h"
#include "jni.h"




JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {
    jvmtiEnv *jvmti = create_jvmti_env(vm);
    init_jvmti_capabilities(jvmti);
    JNIEnv *jni = create_jni_env(vm);
    return JNI_OK;
}


