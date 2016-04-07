/*
 * Main code of Agent library. This code is written in C++11
 */
#include <stdio.h>
#include <string.h>
#include "jvmti.h"
#include "jni.h"

typedef struct {
    /* JVMTI Environment */
    jvmtiEnv* jvmti = 0;
    JNIEnv* jni = 0;
    jboolean vm_is_started = (jboolean) false;
    jboolean vmDead = (jboolean) false;
    JavaVM* jvm = 0;
    /* Data access Lock */
    //jrawMonitorID lock;
} GlobalAgentData;

static GlobalAgentData *gdata;

static void check_jvmti_error(jvmtiEnv *jvmti, jvmtiError errnum, const char *str) {
    if (errnum != JVMTI_ERROR_NONE) {
        char *errnum_str;

        errnum_str = NULL;
        (void) jvmti->GetErrorName(errnum, &errnum_str);

        printf("ERROR: JVMTI: %d(%s): %s\n", errnum,
               (errnum_str == NULL ? "Unknown" : errnum_str),
               (str == NULL ? "" : str));
    }
}

static void JNICALL cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv* env,
        jclass class_being_redefined, jobject loader,
const char* name, jobject protection_domain,
jint class_data_len, const unsigned char* class_data,
        jint* new_class_data_len, unsigned char** new_class_data) {
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

/*
 * Callback we get when the JVM is initialized. We use this time to setup our GC thread
 */
static void JNICALL callbackVMInit(jvmtiEnv * jvmti, JNIEnv * env, jthread thread)
{
jvmtiError err;

//err = jvmti->RunAgentThread(alloc_thread(env), &gcWorker, NULL,
//		JVMTI_THREAD_MAX_PRIORITY);
//check_jvmti_error(jvmti, err, "Unable to run agent cleanup thread");
}

/*
 * Callback we receive when the JVM terminates - no more functions can be called after this
 */
static void JNICALL callbackVMDeath(jvmtiEnv *jvmti_env, JNIEnv* jni_env) {
gdata->vmDead = JNI_TRUE;
}


/*
 * Callback we get when the JVM starts up, but before its initialized.
 * Sets up the JNI calls.
 */
static void JNICALL cbVMStart(jvmtiEnv *jvmti, JNIEnv *env) {
/*	enter_critical_section(jvmti);
	{
		jclass klass;
		jfieldID field;
		jint rc;

		static JNINativeMethod registry[2] = { {"_setTag",
				"(Ljava/lang/Object;Ljava/lang/Object;)V",
				(void*) &setObjExpression}, {"_getTag",
				"(Ljava/lang/Object;)Ljava/lang/Object;",
				(void*) &getObjExpression}};
		*//* Register Natives for class whose methods we use *//*
		klass = env->FindClass("net/jonbell/examples/jvmti/tagging/runtime/Tagger");
		if (klass == NULL) {
			fatal_error(
					"ERROR: JNI: Cannot find Tagger with FindClass\n");
		}
		rc = env->RegisterNatives(klass, registry, 2);
		if (rc != 0) {
			fatal_error(
					"ERROR: JNI: Cannot register natives for Tagger\n");
		}
		*//* Engage calls. *//*
		field = env->GetStaticFieldID(klass, "engaged", "I");
		if (field == NULL) {
			fatal_error("ERROR: JNI: Cannot get field\n"
			);
		}
		env->SetStaticIntField(klass, field, 1);

		*//* Indicate VM has started *//*
		gdata->vm_is_started = JNI_TRUE;

	}
	exit_critical_section(jvmti);*/

}


/*
 * Callback that is notified when our agent is loaded. Registers for event
 * notifications.
 */
JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    static GlobalAgentData data;
    jvmtiError error;
    jint result;
    jvmtiEnv *jvmti = NULL;
    jvmtiEventCallbacks callbacks;
    jvmtiCapabilities capabilities;

    gdata = &data;
    gdata->jvm = jvm;
    result = jvm->GetEnv((void **) &jvmti, JVMTI_VERSION_1_0);

    if (result != JNI_OK || jvmti == NULL) {
        /* This means that the VM was unable to obtain this version of the
         * JVMTI interface, this is a fatal error.
         */
        printf("ERROR: Unable to access JVMTI Version 1 (0x%x),"
                       " is your J2SE a 1.5 or newer version?"
                       " JNIEnv's GetEnv() returned %d\n", JVMTI_VERSION_1, result);

    }
    //save jvmti for later
    gdata->jvmti = jvmti;

    //Register our capabilities
    (void) memset(&capabilities, 0, sizeof(jvmtiCapabilities));
    capabilities.can_signal_thread = 1;
    capabilities.can_generate_object_free_events = 1;
    capabilities.can_tag_objects = 1;
    capabilities.can_generate_garbage_collection_events = 1;
    capabilities.can_generate_all_class_hook_events = 1;

    error = jvmti->AddCapabilities(&capabilities);
    check_jvmti_error(jvmti, error,
                      "Unable to get necessary JVMTI capabilities.");

    //Register callbacks
    (void) memset(&callbacks, 0, sizeof(callbacks));
    callbacks.VMInit = &callbackVMInit;
    callbacks.VMDeath = &callbackVMDeath;
    callbacks.VMStart = &cbVMStart;
    callbacks.ClassFileLoadHook = &cbClassFileLoadHook;

    error = jvmti->SetEventCallbacks(&callbacks, (jint) sizeof(callbacks));
    check_jvmti_error(jvmti, error, "Cannot set jvmti callbacks");

    //Register for events
    error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_INIT,
                                            (jthread) NULL);
    check_jvmti_error(jvmti, error, "Cannot set event notification");

    error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_DEATH,
                                            (jthread) NULL);
    check_jvmti_error(jvmti, error, "Cannot set event notification");

    error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_START,
                                            (jthread) NULL);
    check_jvmti_error(jvmti, error, "Cannot set event notification");

    error = jvmti->SetEventNotificationMode(JVMTI_ENABLE,
                                            JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, (jthread) NULL);
    check_jvmti_error(jvmti, error, "Cannot set event notification");


    return JNI_OK;
}


