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