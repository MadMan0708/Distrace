//
// Created by Jakub Háva on 08/04/16.
//

#include "AgentCallbacks.h"
#include <spdlog/spdlog.h>
#include <nnxx/reqrep.h>
#include <nnxx/message.h>
#include "Logger.h"
#include "Agent.h"

namespace DistraceAgent {
    std::shared_ptr<spdlog::logger> logger = Logger::getLogger("AgentCallbacks");

    void JNICALL AgentCallbacks::cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *env,
                                            jclass class_being_redefined, jobject loader,
                                            const char *name, jobject protection_domain,
                                            jint class_data_len, const unsigned char *class_data,
                                            jint *new_class_data_len, unsigned char **new_class_data) {
        logger->debug() << "The class " << name << " has been loaded";

        // First check if the class should be instrumented for internal purposes. These classes are instrumented right
        // away before they are loaded into JVM ( in case agent is loaded with the start of
        // monitored JVM). No need to ask instrumentor if they should be instrumented.
        if (std::binary_search(Agent::internal_classes_to_instrument.begin(),
                               Agent::internal_classes_to_instrument.end(), name)) {
            logger->info() << "Sending class " << name << " and its bytecode to instrumentor JVM ";
            int numBytes = Agent::globalData->inst_socket.send("ABC",3,0);
            if( numBytes < 0){
                logger->error() << "Couldn't send the bytes to the instrumentor JVM";
            }else{
                logger->info() << "Bytes send "<< numBytes;
                nnxx::message msg = Agent::globalData->inst_socket.recv();
                logger->info() << "Received back " << msg;
            }
        } else {
            // send message to the instrumentor containing name of the class being loaded.
            // instrumentor should reply whether this class needs to be instrumented or not
            // if this class should be intstrumented, send its bytecode to the instrumentor

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