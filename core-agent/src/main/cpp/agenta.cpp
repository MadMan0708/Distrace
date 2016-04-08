/*
 * Main code of Agent library. This code is written in C++11
 */
#include <stdio.h>
#include <string.h>
#include <iostream>
#include "jvmti.h"
#include "jni.h"

typedef struct {
    /* JVMTI Environment */
    jvmtiEnv *jvmti = 0;
    JNIEnv *jni = 0;
    jboolean vm_is_started = (jboolean) false;
    jboolean vmDead = (jboolean) false;
    JavaVM *jvm = 0;
    /* Data access Lock */
    //jrawMonitorID lock;
} GlobalAgentData;

static GlobalAgentData *gdata;



JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *vm, char *options, void *reserved) {
    jvmtiEnv *jvmti = create_jvmti_env(vm);
    init_jvmti_capabilities(jvmti);
    JNIEnv *jni = create_jni_env(vm);
    return JNI_OK;
}



/**
 * Register our capabilities with the JVM
 */
void init_jvmti_capabilities(jvmtiEnv *env) {
    jvmtiCapabilities capabilities;
    //Set all capabilities to zero - not required and explicitly pick ones we need
    (void) memset(&capabilities, 0, sizeof(jvmtiCapabilities));
    capabilities.can_signal_thread = 1;
    capabilities.can_generate_object_free_events = 1;
    capabilities.can_tag_objects = 1;
    capabilities.can_generate_garbage_collection_events = 1;
    capabilities.can_generate_all_class_hook_events = 1;

    jvmtiError error = env->AddCapabilities(&capabilities);
    AgentUtils::check_jvmti_error(env, error,
                                     "Unable to get necessary JVMTI capabilities.");

}


static void JNICALL cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *env,
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

/*
 * Callback we get when the JVM is initialized. We use this time to setup our GC thread
 */
static void JNICALL callbackVMInit(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) {
    jvmtiError err;
    std::cout << "Initialized!";
//err = jvmti->RunAgentThread(alloc_thread(env), &gcWorker, NULL,
//		JVMTI_THREAD_MAX_PRIORITY);
//check_jvmti_error(jvmti, err, "Unable to run agent cleanup thread");
}

/*
 * Callback we receive when the JVM terminates - no more functions can be called after this
 */
static void JNICALL callbackVMDeath(jvmtiEnv *jvmti_env, JNIEnv *jni_env) {
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
