//
// Created by Jakub Háva on 08/04/16.
//

#include "AgentCallbacks.h"
#include <spdlog/spdlog.h>

#include "Logger.h"
namespace DistraceAgent {
    std::shared_ptr<spdlog::logger> logger = Logger::getLogger("AgentCallbacks");

    void JNICALL AgentCallbacks::cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *env,
                                            jclass class_being_redefined, jobject loader,
                                            const char *name, jobject protection_domain,
                                            jint class_data_len, const unsigned char *class_data,
                                            jint *new_class_data_len, unsigned char **new_class_data) {
        logger->debug() << "The class "<< name << " has been loaded";
        // send message to the instrumentor name of the class being loaded.


        // we need to instrument some classes for internal purposes. These classes are instrumented right away
        // before they are loaded into JVM.
        if(std::binary_search(Agent::internal_classes_to_instrument.begin(), Agent::internal_classes_to_instrument.end(), name)){
            logger->info() << "Sending class "<< name << " and its bytecode to instrumentor JVM ";
        }

    }

    void JNICALL AgentCallbacks::callbackVMInit(jvmtiEnv *jvmti, JNIEnv *env, jthread thread) {
        logger->info("The virtual machine has been initialized!");
    }

    void JNICALL AgentCallbacks::callbackVMDeath(jvmtiEnv *jvmti_env, JNIEnv *jni_env) {
        Agent::globalData->vm_dead = JNI_TRUE;
        logger->info("The virtual machine has been terminated!");
    }

    void JNICALL AgentCallbacks::cbVMStart(jvmtiEnv *jvmti, JNIEnv *env) {
        logger->info("The virtual machine has been started!");
    }
}