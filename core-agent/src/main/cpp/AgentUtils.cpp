//
// Created by Jakub Háva on 08/04/16.
//

#include <cstring>
#include <iostream>
#include "AgentUtils.h"
#include "AgentCallbacks.h"

namespace DistraceAgent {

    int AgentUtils::check_jvmti_error(jvmtiEnv *env, jvmtiError error_number, const char *error_description) {
        if (error_number != JVMTI_ERROR_NONE) {
            char *error_name = NULL;
            env->GetErrorName(error_number, &error_name);

            printf("ERROR: JVMTI: %d(%s): %s\n", error_number,
                   (error_name == NULL ? "Unknown" : error_name),
                   (error_description == NULL ? "" : error_description));
            return JNI_ERR;
        }
        return JNI_OK;
    }

    void AgentUtils::register_jvmti_capabilities(jvmtiEnv *jvmti) {
        jvmtiCapabilities capabilities;
        //Wipe all capabilities and explicitly pick the ones we need
        (void) memset(&capabilities, 0, sizeof(jvmtiCapabilities));
        capabilities.can_signal_thread = 1;
        capabilities.can_generate_object_free_events = 1;
        capabilities.can_tag_objects = 1;
        capabilities.can_generate_all_class_hook_events = 1;

        jvmtiError error = jvmti->AddCapabilities(&capabilities);
        AgentUtils::check_jvmti_error(jvmti, error,  "Unable to get necessary JVMTI capabilities.");

    }


    void AgentUtils::register_jvmti_callbacks(jvmtiEnv *jvmti) {
        jvmtiError error;
        //Wipe all callbacks and explicitly register the callbacks we need
        jvmtiEventCallbacks callbacks;
        (void) memset(&callbacks, 0, sizeof(callbacks));
        callbacks.VMInit = &AgentCallbacks::callbackVMInit;
        callbacks.VMDeath = &AgentCallbacks::callbackVMDeath;
        callbacks.VMStart = &AgentCallbacks::cbVMStart;
        callbacks.ClassFileLoadHook = &AgentCallbacks::cbClassFileLoadHook;

        error = jvmti->SetEventCallbacks(&callbacks, (jint) sizeof(callbacks));
        AgentUtils::check_jvmti_error(jvmti, error, "Cannot set JVMTI callbacks");
    }

    void AgentUtils::register_jvmti_events(jvmtiEnv *jvmti) {
        jvmtiError error;
        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_INIT, (jthread) NULL);
        AgentUtils::check_jvmti_error(jvmti, error, "Cannot set event notification for VM_INIT");

        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_DEATH, (jthread) NULL);
        AgentUtils::check_jvmti_error(jvmti, error, "Cannot set event notification for VM_DEATH");

        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_START, (jthread) NULL);
        AgentUtils::check_jvmti_error(jvmti, error, "Cannot set event notification for VM_START");

        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK,
                                                                   (jthread) NULL);
        AgentUtils::check_jvmti_error(jvmti, error,
                                      "Cannot set event notification for CLASS_FILE_LOAD_HOOK");
    }

    int AgentUtils::create_JVMTI_env(JavaVM *jvm, jvmtiEnv *jvmti) {
        jint result =jvm->GetEnv((void **) &jvmti, JVMTI_VERSION_1_2);
        if (result != JNI_OK || jvmti == NULL) {
            /* This means that the VM was unable to obtain this version of the
             * JVMTI interface, this is a fatal error.
             */
            printf("ERROR: Unable to access JVMTI Version 1.2 (0x%x),"
                           " is your J2SE a 1.5 or newer version?"
                           " JNIEnv's GetEnv() returned %d\n", JVMTI_VERSION_1_2, result);
            return JNI_ERR;

        }
        Agent::globalData->jvmti=jvmti;
        return JNI_OK;
    }

    int AgentUtils::create_JNI_env(JavaVM *jvm, JNIEnv *jni) {
        jint result = jvm->GetEnv((void **) &jni, JNI_VERSION_1_6);
        if (result != JNI_OK || jni == NULL) {
            /* This means that the VM was unable to obtain this version of the
             * JNI interface, this is a fatal error.
             */
            printf("ERROR: Unable to access JNI Version 1.6 (0x%x),"
                           " is your J2SE a 1.5 or newer version?"
                           " JNIEnv's GetEnv() returned %d\n", JNI_VERSION_1_6, result);
            return JNI_ERR;

        }
        return JNI_OK;
    }

    int AgentUtils::init_agent(){
        if (AgentUtils::create_JVMTI_env(Agent::globalData->jvm, Agent::globalData->jvmti) == JNI_ERR) {
            return JNI_ERR;
        };
        AgentUtils::register_jvmti_capabilities(Agent::globalData->jvmti);
        AgentUtils::register_jvmti_callbacks(Agent::globalData->jvmti);
        AgentUtils::register_jvmti_events(Agent::globalData->jvmti);
        return JNI_OK;
    }
}
