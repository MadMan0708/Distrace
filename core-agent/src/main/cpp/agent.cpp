//
// Created by Jakub Háva on 08/04/16.
//

#include <cstring>
#include "Agent.h"
#include "AgentUtils.h"
namespace DistraceAgent {

    void Agent::register_jvmti_capabilities() {
        jvmtiCapabilities capabilities;
        //Wipe all capabilities and explicitly pick the ones we need
        (void) memset(&capabilities, 0, sizeof(jvmtiCapabilities));
        capabilities.can_signal_thread = 1;
        capabilities.can_generate_object_free_events = 1;
        capabilities.can_tag_objects = 1;
        capabilities.can_generate_garbage_collection_events = 1;
        capabilities.can_generate_all_class_hook_events = 1;

        jvmtiError error = jvmti->AddCapabilities(&capabilities);
        AgentUtils::check_jvmti_error(jvmti, error,
                                      "Unable to get necessary JVMTI capabilities.");

    }

    JNIEXPORT jint JNICALL Agent::Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
        this->jvm=jvm;
        if(create_JVMTI_env()==JNI_ERR){
            return JNI_ERR;
        };

        register_jvmti_capabilities();
        register_jvmti_callbacks();
        register_jvmti_events();
        return JNI_OK;
    }

    void Agent::register_jvmti_callbacks() {
        jvmtiError error;
        //Wipe all callbacks and explicitly register the callbacks we need
        jvmtiEventCallbacks callbacks;
        (void) memset(&callbacks, 0, sizeof(callbacks));
        callbacks.VMInit = &callbackVMInit;
        callbacks.VMDeath = &callbackVMDeath;
        callbacks.VMStart = &cbVMStart;
        callbacks.ClassFileLoadHook = &cbClassFileLoadHook;

        error = jvmti->SetEventCallbacks(&callbacks, (jint) sizeof(callbacks));
        AgentUtils::check_jvmti_error(jvmti, error, "Cannot set JVMTI callbacks");
    }

    void Agent::register_jvmti_events() {
        jvmtiError error;
        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_INIT, (jthread) NULL);
        AgentUtils::check_jvmti_error(jvmti, error, "Cannot set event notification for VM_INIT");

        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_DEATH, (jthread) NULL);
        AgentUtils::check_jvmti_error(jvmti, error, "Cannot set event notification for VM_DEATH");

        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_START, (jthread) NULL);
        AgentUtils::check_jvmti_error(jvmti, error, "Cannot set event notification for VM_START");

        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, (jthread) NULL);
        AgentUtils::check_jvmti_error(jvmti, error, "Cannot set event notification for CLASS_FILE_LOAD_HOOK");
    }

    int Agent::create_JVMTI_env() {
        jint result = jvm->GetEnv((void **) &jvmti, JVMTI_VERSION_1_2);
        if (result != JNI_OK || jvmti == NULL) {
            /* This means that the VM was unable to obtain this version of the
             * JVMTI interface, this is a fatal error.
             */
            printf("ERROR: Unable to access JVMTI Version 1.2 (0x%x),"
                           " is your J2SE a 1.5 or newer version?"
                           " JNIEnv's GetEnv() returned %d\n", JVMTI_VERSION_1_2, result);
            return JNI_ERR;

        }
        return JNI_OK;
    }

    int Agent::create_JNI_env() {
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
}

