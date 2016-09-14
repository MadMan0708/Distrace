//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>
#include "AgentCallbacks.h"
#include "AgentUtils.h"
#include "JavaUtils.h"

using namespace Distrace;
using namespace Distrace::Logging;

    void JNICALL AgentCallbacks::cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *env,
                                            jclass class_being_redefined, jobject loader,
                                            const char *name, jobject protection_domain,
                                            jint class_data_len, const unsigned char *class_data,
                                            jint *new_class_data_len, unsigned char **new_class_data) {

        //TODO: Improve this. Attaching and deattaching JNIafter each request is quite costly
        // Don't handle classes which are being loaded during vm initialization and the ones loaded by ignored class loaders
        if(Agent::globalData->vm_started){
            int attachStatus = AgentUtils::JNI_AttachCurrentThread(env);
            auto loader_name = JavaUtils::getClassLoaderName(env, loader);
            if(!JavaUtils::isIgnoredClassLoader(loader_name)){
                log(LOGGER_AGENT_CALLBACKS)->info() << "The class " << name << " is about to be loaded by \"" << loader_name << "\" class loader ";

                jclass instrumentor = env->FindClass("cz/cuni/mff/d3s/distrace/examples/InstrumentorStarter");
                jmethodID constructor = env->GetMethodID(instrumentor, "<init>", "()V");
                jobject instance = env->NewObject(instrumentor, constructor);
                jmethodID instrumentMethod = env->GetMethodID(instrumentor, "instrument","(Ljava/lang/String;[B)[B");

                auto bytes_for_java = env->NewByteArray(class_data_len);
                env->SetByteArrayRegion(bytes_for_java, 0, class_data_len, (jbyte*) class_data);
                jstring name_for_java = env->NewStringUTF(name);

                jbyteArray java_bytes_type_desc = (jbyteArray)env->CallObjectMethod(instance, instrumentMethod, name_for_java, bytes_for_java);
                if(java_bytes_type_desc != NULL){
                    auto typeD = JavaUtils::as_unsigned_char_array(env, java_bytes_type_desc);
                    jsize len = env->GetArrayLength(java_bytes_type_desc);

                    *new_class_data_len = len;
                    *new_class_data = typeD;
                }
            }
            AgentUtils::dettach_JNI_from_current_thread(attachStatus);
          }
    }


    void JNICALL AgentCallbacks::callbackVMInit(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) {
      // this forces JVM to load this class in the initialization phase
      env->FindClass("cz/cuni/mff/d3s/distrace/utils/ByteCodeClassLoader");
      Agent::globalData->vm_started = JNI_TRUE;
      log(LOGGER_AGENT_CALLBACKS)->info("The virtual machine has been initialized!");
    }

    void JNICALL AgentCallbacks::callbackVMDeath(jvmtiEnv *jvmti_env, JNIEnv *jni_env) {
        Agent::globalData->vm_dead = JNI_TRUE;
        // stop the instrumentor JVM
        log(LOGGER_AGENT_CALLBACKS)->info("The virtual machine has been terminated!");
    }

    void JNICALL AgentCallbacks::cbVMStart(jvmtiEnv *jvmti, JNIEnv *env) {
        log(LOGGER_AGENT_CALLBACKS)->info("The virtual machine has been started!");
    }

    void JNICALL AgentCallbacks::cbClassLoad(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jclass klass){
        log(LOGGER_AGENT_CALLBACKS)->info() << "Class: \""<< JavaUtils::getClassName(jni_env, klass) <<"\" loaded";
    }

    void JNICALL AgentCallbacks::cbClassPrepare(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jclass klass){
        log(LOGGER_AGENT_CALLBACKS)->info() << "Class: \""<< JavaUtils::getClassName(jni_env, klass) <<"\" prepared";
    }