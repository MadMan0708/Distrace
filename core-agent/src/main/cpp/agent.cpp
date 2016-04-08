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

        jvmtiError error = globalData->jvmti->AddCapabilities(&capabilities);
        AgentUtils::check_jvmti_error(globalData->jvmti, error,
                                      "Unable to get necessary JVMTI capabilities.");

    }

    JNIEXPORT jint JNICALL Agent::Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
        static GlobalAgentData data;
        globalData = &data;
        globalData->jvm = jvm;
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

        error = globalData->jvmti->SetEventCallbacks(&callbacks, (jint) sizeof(callbacks));
        AgentUtils::check_jvmti_error(globalData->jvmti, error, "Cannot set JVMTI callbacks");
    }

    void Agent::register_jvmti_events() {
        jvmtiError error;
        error = globalData->jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_INIT, (jthread) NULL);
        AgentUtils::check_jvmti_error(globalData->jvmti, error, "Cannot set event notification for VM_INIT");

        error = globalData->jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_DEATH, (jthread) NULL);
        AgentUtils::check_jvmti_error(globalData->jvmti, error, "Cannot set event notification for VM_DEATH");

        error = globalData->jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_START, (jthread) NULL);
        AgentUtils::check_jvmti_error(globalData->jvmti, error, "Cannot set event notification for VM_START");

        error = globalData->jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, (jthread) NULL);
        AgentUtils::check_jvmti_error(globalData->jvmti, error, "Cannot set event notification for CLASS_FILE_LOAD_HOOK");
    }

    int Agent::create_JVMTI_env() {
        jint result = globalData->jvm->GetEnv((void **) &globalData->jvmti, JVMTI_VERSION_1_2);
        if (result != JNI_OK || globalData->jvmti == NULL) {
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
        jint result = globalData->jvm->GetEnv((void **) &globalData->jni, JNI_VERSION_1_6);
        if (result != JNI_OK || globalData->jni == NULL) {
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
    static void JNICALL Agent::cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *env,
                                                            jclass class_being_redefined, jobject loader,
                                                            const char *name, jobject protection_domain,
                                                            jint class_data_len, const unsigned char *class_data,
                                                            jint *new_class_data_len, unsigned char **new_class_data) {
/*    enterCriticalSection(jvmti); {
	if ( !gdata->vmDead ) {
	    const char \* classname;
	    if ( name == NULL ) {
		classname = java_crw_demo_classname(class_data, class_data_len,
				NULL);
            } else {
	        classname = strdup(name);
            }
	    \*new_class_data_len = 0;
            \*new_class_data     = NULL;
            if ( strcmp(classname, STRING(HEAP_TRACKER_class)) != 0 ) {
                jint           cnum;
                int            systemClass;
                unsigned char \*newImage;
                long           newLength;

                cnum = gdata->ccount++;
                systemClass = 0;
                if ( !gdata->vmStarted ) {
                    systemClass = 1;
                }
                newImage = NULL;
                newLength = 0;

                java_crw_demo(cnum,
                    classname,
                    class_data,
                    class_data_len,
                    systemClass,
                    STRING(HEAP_TRACKER_class),
                    "L" STRING(HEAP_TRACKER_class) ";",
                    NULL, NULL,
                    NULL, NULL,
                    STRING(HEAP_TRACKER_newobj), "(Ljava/lang/Object;)V",
                    STRING(HEAP_TRACKER_newarr), "(Ljava/lang/Object;)V",
                    &newImage,
                    &newLength,
                    NULL,
                    NULL);
                if ( newLength > 0 ) {
                    unsigned char \*jvmti_space;

                    jvmti_space = (unsigned char \*)allocate(jvmti, (jint)newLength);
                    (void)memcpy((void\*)jvmti_space, (void\*)newImage, (int)newLength);
                    \*new_class_data_len = (jint)newLength;
                    \*new_class_data     = jvmti_space; /\* VM will deallocate \*//*
                }
                if ( newImage != NULL ) {
                    (void)free((void\*)newImage);
                }
            }
	    (void)free((void\*)classname);
	}
    } exitCriticalSection(jvmti);*/
    }

    static void JNICALL Agent::callbackVMInit(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) {
    }

    static void JNICALL Agent::callbackVMDeath(jvmtiEnv *jvmti_env, JNIEnv *jni_env) {
        globalData->vm_dead = JNI_TRUE;
    }

    static void JNICALL Agent::cbVMStart(jvmtiEnv *jvmti, JNIEnv *env) {
    }
}

