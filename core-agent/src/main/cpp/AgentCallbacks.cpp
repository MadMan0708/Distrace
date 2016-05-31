//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>
#include "AgentCallbacks.h"
#include <spdlog/spdlog.h>
#include <nnxx/reqrep.h>
#include <nnxx/message.h>
#include <future>
#include "AgentUtils.h"
#include "JavaUtils.h"
#include "Agent.h"

using namespace Distrace;
using namespace Distrace::Logging;

    void JNICALL AgentCallbacks::cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *env,
                                            jclass class_being_redefined, jobject loader,
                                            const char *name, jobject protection_domain,
                                            jint class_data_len, const unsigned char *class_data,
                                            jint *new_class_data_len, unsigned char **new_class_data) {

        //TODO: Improve - attaching and deattaching after each request is quite costly - Write this to docs
        int attachStatus = AgentUtils::JNI_AttachCurrentThread(env);
        auto loader_name = JavaUtils::getClassLoaderName(env, loader);
        log(LOGGER_AGENT_CALLBACKS)->debug() << "The class " << name << " is about to be loaded by \"" << loader_name << "\" class loader ";
        AgentUtils::JNI_DettachCurrentThread(attachStatus);

         if(Agent::globalData->inst_api->should_instrument(name)){
             *new_class_data_len = Agent::globalData->inst_api->instrument(class_data, class_data_len, new_class_data);
             log(LOGGER_AGENT_CALLBACKS)->info() << "The class " << name << " has been instrumented";
         }
    }


    void JNICALL AgentCallbacks::callbackVMInit(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) {
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