//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>
#include "AgentCallbacks.h"
#include "AgentUtils.h"
#include "JavaUtils.h"
#include "Agent.h"
#include "bytecode/ClassParser.h"

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

        log(LOGGER_AGENT_CALLBACKS)->info() << "BEFORE LOADING: The class " << name <<
        " is about to be loaded by \""
        << loader_name << "\" class loader ";

            if (!(JavaUtils::isIgnoredClassLoader(loader_name) || Agent::globalData->inst_api->is_aux_class(name))) {

                // Send class name to the instrumentor and check if this class is available. Don't sent the original
                // bytecode if it's already available, otherwise send it.
                if(Agent::globalData->inst_api->has_class(name)){
                    log(LOGGER_AGENT_CALLBACKS)->debug() << "Instrumentor already has class: " << name;
                } else {
                    log(LOGGER_AGENT_CALLBACKS)->info() << "Sending original bytecode to the instrumentor: " << name;
                    // send bytecode for current class
                    Agent::globalData->inst_api->send_byte_code(name, class_data, class_data_len);
                }

                log(LOGGER_AGENT_CALLBACKS)->info() << "Send bytecode for all the dependencies for class: " << name;
                loadDependencies(env, loader, name, class_data, class_data_len);
                // once we have all the dependencies in the instrumentor JVM, instrument the class
                instrument(name, new_class_data, new_class_data_len, loader_name);
            }

        log(LOGGER_AGENT_CALLBACKS)->info() << "AFTER LOADING: The class " << name << " has been loaded by \""
        << loader_name << "\" class loader";
        AgentUtils::dettach_JNI_from_current_thread(attachStatus);
    }
}

void AgentCallbacks::loadDependencies(JNIEnv *env, jobject loader, const char *name, const unsigned char *class_data, jint class_data_len){
    std::vector<std::string> types = ClassParser::parse(name, class_data, class_data_len);
    for(std::vector<std::string>::iterator it=types.begin() ; it <types.end(); it++) {
        // load all dependencies for class
        std::string className = *it;
        className = JavaUtils::toNameWithDots(className);
        log(LOGGER_AGENT_CALLBACKS)->info() << "Loading type: " << className;
        JavaUtils::loadClass(env, loader, className.c_str());
    }
}

void AgentCallbacks::instrument(const char *name, unsigned char **new_class_data, jint *new_class_data_len, std::string loader_name){
    log(LOGGER_AGENT_CALLBACKS)->info() << " About to instrument class: " << name;
    // send instrumentor just name because it already has the class
    if (Agent::globalData->inst_api->should_instrument(name)) {
        // receive reply when we expect the byte code to be instrumented
        *new_class_data_len = Agent::globalData->inst_api->instrument(new_class_data);
        log(LOGGER_AGENT_CALLBACKS)->info() << "The class " << name << " has been instrumented " << loader_name;
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