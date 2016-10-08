//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>
#include "AgentCallbacks.h"
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

    //TODO: Improve this. Attaching and deattaching JNI after each request is quite costly
    // Don't handle classes which are being loaded during vm initialization and the ones loaded by ignored class loaders
    if (Agent::globalData->vm_started) {
        int attachStatus = AgentUtils::JNI_AttachCurrentThread(env);
        auto loader_name = JavaUtils::getClassLoaderName(env, loader);

        log(LOGGER_AGENT_CALLBACKS)->debug() << "BEFORE LOADING: The class " << name <<
        " is about to be loaded by \""
        << loader_name << "\" class loader ";


            if (!JavaUtils::isIgnoredClassLoader(loader_name)) {
                if(!JavaUtils::isAlreadyLoaded(env, name)) {

                // send class name to instrumentor and check if this class is available. If it is then we send here
                // instrumented code without sending byte code there. Otherwise we send bytecode there as well.
                if (Agent::globalData->inst_api->has_class(name)) {

                    log(LOGGER_AGENT_CALLBACKS)->info() << "Instrumentor has class " << name;
                    // send instrumentor just name because it already has the class
                    if (Agent::globalData->inst_api->should_instrument(name)) {
                        // receive reply when we expect the byte code to be instrumented
                        *new_class_data_len = Agent::globalData->inst_api->instrument(new_class_data);
                        log(LOGGER_AGENT_CALLBACKS)->info() << "The class " << name << " has been instrumented " <<
                        loader_name;
                    }
                } else {

                    log(LOGGER_AGENT_CALLBACKS)->info() << "Instrumentor does not have class " << name;
                    // load class and all its dependencies
                    bool should_continue = JavaUtils::forceLoadClass(env, name, class_data, class_data_len);
                    //  if(should_continue) {
                    log(LOGGER_AGENT_CALLBACKS)->debug() << "Continue after force load for" << name;
                    // send instrumentor byte code and class name
                    if (Agent::globalData->inst_api->should_instrument(name, class_data, class_data_len)) {
                        // receive reply when we expect the byte code to be instrumented
                        *new_class_data_len = Agent::globalData->inst_api->instrument(new_class_data);
                        log(LOGGER_AGENT_CALLBACKS)->info() << "The class " << name << " has been instrumented " <<
                        loader_name;
                    }
                    //  }
                }
            }else{
                    log(LOGGER_AGENT_CALLBACKS)->debug() << "Ignoring repeated loading of class " << name
                    << " with classloader " << loader_name;
                    // send byte code of class to instrumentor, but do not the any instrumentation. This is for the case
                    // of cyclic dependency ( we can't load the cyclic dependency, but we need to provide byte code for it.

                    // also check if the instrumentor has the class already, since it doesn't make sense to send bytecode
                    // which is already stored on the instrumenter

                    //TODO: check whether class is alreaady on classlaoder search path
                    //if(!Agent::globalData->inst_api->has_class(name)){
                        //send the bytecode
                        Agent::globalData->inst_api->send_byte_code(name, class_data, class_data_len);

                    //}
                }
        }

        log(LOGGER_AGENT_CALLBACKS)->debug() << "AFTER LOADING: The class " << name << " has been loaded by \""
        << loader_name << "\" class loader";
        AgentUtils::dettach_JNI_from_current_thread(attachStatus);
    }
}


void JNICALL AgentCallbacks::callbackVMInit(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) {
    // this forces JVM to load this class in the initialization phase
    env->FindClass("cz/cuni/mff/d3s/distrace/Utils");
    Agent::globalData->vm_started = JNI_TRUE;
    log(LOGGER_AGENT_CALLBACKS)->info("The virtual machine has been initialized!");
}

void JNICALL AgentCallbacks::callbackVMDeath(jvmtiEnv *jvmti_env, JNIEnv *jni_env) {
    Agent::globalData->vm_dead = JNI_TRUE;
    // stop the instrumentor JVM ( only in local mode )
    if (Agent::getArgs()->is_running_in_local_mode()) {
        Agent::globalData->inst_api->stop();
    }

    log(LOGGER_AGENT_CALLBACKS)->info("The virtual machine has been terminated!");
}

void JNICALL AgentCallbacks::cbVMStart(jvmtiEnv *jvmti, JNIEnv *env) {
    log(LOGGER_AGENT_CALLBACKS)->info("The virtual machine has been started!");
}

void JNICALL AgentCallbacks::cbClassLoad(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread, jclass klass) {
    log(LOGGER_AGENT_CALLBACKS)->info() << "Class: \"" << JavaUtils::getClassName(jni_env, klass) << "\" loaded";
}

void JNICALL AgentCallbacks::cbClassPrepare(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread, jclass klass) {
    log(LOGGER_AGENT_CALLBACKS)->info() << "Class: \"" << JavaUtils::getClassName(jni_env, klass) << "\" prepared";
}