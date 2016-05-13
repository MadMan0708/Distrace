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
#include "Logger.h"
#include "AgentUtils.h"
#include "JavaUtils.h"

namespace DistraceAgent {
    std::shared_ptr<spdlog::logger> logger = Logger::getLogger("AgentCallbacks");

    void JNICALL AgentCallbacks::cbClassFileLoadHook(jvmtiEnv *jvmti, JNIEnv *env,
                                            jclass class_being_redefined, jobject loader,
                                            const char *name, jobject protection_domain,
                                            jint class_data_len, const unsigned char *class_data,
                                            jint *new_class_data_len, unsigned char **new_class_data) {

        //TODO: Improve - attaching and dettaching after each request is quite costly - Write this to docs
        int attachStatus = AgentUtils::JNI_AttachCurrentThread(env);
        auto cl_name = JavaUtils::getClassLoaderName(env, loader);
        logger->debug() << "The class " << name << " is about to be loaded by \"" << cl_name << "\" class loader ";
        AgentUtils::JNI_DettachCurrentThread(attachStatus);
        // First check if the class should be instrumented for internal purposes. These classes are instrumented right
        // away before they are loaded into JVM ( in case agent is loaded with the start of
        // monitored JVM). No need to ask instrumentor if they should be instrumented.
        if (std::binary_search(Agent::internal_classes_to_instrument.begin(),
                               Agent::internal_classes_to_instrument.end(), name)) {

            std::string instrumentor_jar = Agent::globalData->agent_args->find(Agent::ARG_LOG_LEVEL)->second;
            jvmtiError er = Agent::globalData->jvmti->AddToSystemClassLoaderSearch(instrumentor_jar.c_str());
            AgentUtils::check_jvmti_error(Agent::globalData->jvmti,er,"Cannon add to bootstrap classloader search path");



            logger->info() << "Sending class " << name << " and its bytecode to instrumentor JVM ";
            //send name
            int numBytes = Agent::globalData->inst_socket.send(name,strlen(name),0);
            if( numBytes < 0){
                logger->error() << "Couldn't send the bytes to the instrumentor JVM";
            }else{
                logger->info() << "Bytes send "<< numBytes;
                nnxx::message msg = Agent::globalData->inst_socket.recv();
                logger->info() << "Received back " << msg;
            }
            // send bytecode
            numBytes = Agent::globalData->inst_socket.send(class_data,class_data_len,0);
            if( numBytes < 0){
                logger->error() << "Couldn't send the bytes to the instrumentor JVM";
            }else{
                logger->info() << "Bytes send "<< numBytes;
                nnxx::message msg = Agent::globalData->inst_socket.recv();
                logger->info() << "Received back " << msg;
            }
            numBytes = Agent::globalData->inst_socket.send("LENGTH");
            if( numBytes < 0){
                logger->error() << "Couldn't send the bytes to the instrumentor JVM";
            }else{
                logger->info() << "Bytes send "<< numBytes;
                std::string str =  Agent::globalData->inst_socket.recv<std::string>();
                *new_class_data_len = std::atoi(str.c_str());
                logger->info() << "Received back the length " << *new_class_data_len;

            }

            numBytes = Agent::globalData->inst_socket.send("GET",3);
            if( numBytes < 0){
                logger->error() << "Couldn't send the bytes to the instrumentor JVM";
            }else{
                logger->info() << "Bytes send "<< numBytes;
                *new_class_data  = new unsigned char[*new_class_data_len];
                numBytes = Agent::globalData->inst_socket.recv(*new_class_data,*new_class_data_len);
                logger->info() << "Received data length after calling recv method " << numBytes;
            }

        } else {
            // send message to the instrumentor containing name of the class being loaded.
            // instrumentor should reply whether this class needs to be instrumented or not
            // if this class should be intstrumented, send its bytecode to the instrumentor

        }
    }

    void JNICALL AgentCallbacks::cbClassLoad(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jclass klass){
        logger->info() << "Class: \""<< JavaUtils::getClassName(jni_env, klass) <<"\" loaded";
    }

    void JNICALL AgentCallbacks::cbClassPrepare(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread, jclass klass){
        logger->info() << "Class: \""<< JavaUtils::getClassName(jni_env, klass) <<"\" prepared";
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