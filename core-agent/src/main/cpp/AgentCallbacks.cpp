//
// Created by Jakub Háva on 08/04/16.
//
#include <jni.h>
#include <jvmti.h>
#include "AgentCallbacks.h"
#include "AgentUtils.h"
#include "JavaUtils.h"
#include "bytecode/ClassParser.h"

using namespace Distrace;
using namespace Distrace::Logging;

void JNICALL AgentCallbacks::cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *env,
                                                 jclass class_being_redefined, jobject loader,
                                                 const char *name, jobject protection_domain,
                                                 jint class_data_len, const unsigned char *class_data,
                                                 jint *new_class_data_len, unsigned char **new_class_data) {

    AgentUtils::enter_critical_section(jvmti);
    // Don't handle classes which are being loaded during vm initialization
    // and the ones loaded by ignored class loaders
    if (Agent::globalData->vm_started) {
        int attachStatus = AgentUtils::JNI_AttachCurrentThread(env);
        auto loader_name = JavaUtils::getClassLoaderName(env, loader);
        log(LOGGER_AGENT_CALLBACKS)->info("BEFORE LOADING: {} is about to be loaded by {} class loader", name, loader_name);

        if (!(JavaUtils::isIgnoredClassLoader(loader_name) || Agent::globalData->inst_api->is_aux_class(name))) {

            // Send class name to the instrumentor and check if this class is available.
            // Don't send the original bytecode if it's already available.
            if(Agent::globalData->inst_api->has_class(name)){
                log(LOGGER_AGENT_CALLBACKS)->debug("Instrumentor already contains : {}", name);
            } else {
                log(LOGGER_AGENT_CALLBACKS)->info("Sending original bytecode to the instrumentor: {}", name);
                // send bytecode for current class
                Agent::globalData->inst_api->send_byte_code(name, class_data, class_data_len);
            }
            loadDependencies(env, loader, name, class_data, class_data_len);
            instrument(name, new_class_data, new_class_data_len);
        }
        log(LOGGER_AGENT_CALLBACKS)->info("AFTER LOADING: {} loaded by {}", name, loader_name);
        AgentUtils::dettach_JNI_from_current_thread(attachStatus);
    }
    AgentUtils::exit_critical_section(jvmti);
}

void AgentCallbacks::loadDependencies(JNIEnv *env, jobject loader, const char *name, const unsigned char *class_data, jint class_data_len){
    log(LOGGER_BYTECODE)->info("Parsing: {}", name);
    ClassParser* parser = ClassParser::parse(class_data, class_data_len);
    auto allRefs = parser->getAllRefs();
    for(auto &ref : allRefs){
        log(LOGGER_BYTECODE)->info("Found -> {}", ref);
        if(InstrumentorAPI::isGoodType(name, ref)){
            unsigned char *classBytes;
            int classBytesLen = Agent::globalData->inst_api->sendReferencedClass(env, ref, loader, &classBytes);
            if(classBytesLen!=0) {
                // if class bytes for the previous class hasn't been sent, proceed with recursion
                loadDependencies(env, loader, ref.c_str(), classBytes, classBytesLen);
            }
        }
    }
}

void AgentCallbacks::instrument(const char *name, unsigned char **new_class_data, jint *new_class_data_len){
    log(LOGGER_AGENT_CALLBACKS)->info("About to instrument class: {}", name);
    // send instrumentor just name because it already has the class
    if (Agent::globalData->inst_api->should_instrument(name)) {
        // receive reply when we expect the byte code to be instrumented
        *new_class_data_len = Agent::globalData->inst_api->instrument(new_class_data);
        log(LOGGER_AGENT_CALLBACKS)->info("The class {} has been instrumented.", name);
    }
}

void JNICALL AgentCallbacks::callbackVMInit(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) {
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
    log(LOGGER_AGENT_CALLBACKS)->debug("Class: {} loaded",  JavaUtils::getClassName(jni_env, klass));
}

void JNICALL AgentCallbacks::cbClassPrepare(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread, jclass klass) {
    log(LOGGER_AGENT_CALLBACKS)->debug("Class: {} prepared",  JavaUtils::getClassName(jni_env, klass));}