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

                if(Agent::globalData->inst_api->should_instrument(name, class_data, class_data_len)){
                    *new_class_data_len = Agent::globalData->inst_api->instrument(new_class_data);
                    log(LOGGER_AGENT_CALLBACKS)->info() << "The class " << name << " has been instrumented";
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
        Agent::globalData->inst_api->stop();
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