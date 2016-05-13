//
// Created by Jakub Háva on 08/04/16.
//

#include <cstring>
#include <iostream>
#include <spdlog/logger.h>
#include <future>
#include <jvmti.h>
#include "AgentUtils.h"
#include "AgentCallbacks.h"
#include "Logger.h"

namespace DistraceAgent {
    std::shared_ptr<spdlog::logger> AgentUtils::logger = Logger::getLogger("AgentUtils");

    int AgentUtils::check_jvmti_error(jvmtiEnv *env, jvmtiError error_number, std::string ok_description, std::string error_description) {
        if (error_number != JVMTI_ERROR_NONE) {
            char *error_name = NULL;
            env->GetErrorName(error_number, &error_name);

            logger->error() << "JVMTI ERROR "<< error_number << " - "
            << (error_name == NULL ? "Unknown" : error_name)
            << ": " <<  (error_description.empty() ? "" : error_description);
            return JNI_ERR;
        }
        if(!ok_description.empty()){
            logger->info() << ok_description;
        }
        return JNI_OK;
    }

    int AgentUtils::check_jvmti_error(jvmtiEnv *env, jvmtiError error_number, std::string error_description) {
        return AgentUtils::check_jvmti_error(env, error_number, "", error_description);
    }

    int AgentUtils::register_jvmti_capabilities(jvmtiEnv *jvmti) {
        jvmtiCapabilities capabilities;
        //Wipe all capabilities and explicitly pick the ones we need
        (void) memset(&capabilities, 0, sizeof(jvmtiCapabilities));
        capabilities.can_signal_thread = 1;
        capabilities.can_generate_object_free_events = 1;
        capabilities.can_tag_objects = 1;
        capabilities.can_generate_all_class_hook_events = 1;
        capabilities.can_retransform_classes = 1;
        capabilities.can_retransform_any_class = 1;

        jvmtiError error = jvmti->AddCapabilities(&capabilities);
        return AgentUtils::check_jvmti_error(jvmti, error,
                                             "JVMTI capabilities registered successfully",
                                             "Unable to get necessary JVMTI capabilities.");

    }

    int AgentUtils::register_jvmti_callbacks(jvmtiEnv *jvmti) {
        //Wipe all callbacks and explicitly register the callbacks we need
        jvmtiEventCallbacks callbacks;
        (void) memset(&callbacks, 0, sizeof(callbacks));
        callbacks.VMInit = &AgentCallbacks::callbackVMInit;
        callbacks.VMDeath = &AgentCallbacks::callbackVMDeath;
        callbacks.VMStart = &AgentCallbacks::cbVMStart;
        callbacks.ClassLoad = &AgentCallbacks::cbClassLoad;
        callbacks.ClassPrepare = &AgentCallbacks::cbClassPrepare;
        callbacks.ClassFileLoadHook = &AgentCallbacks::cbClassFileLoadHook;

        jvmtiError error = jvmti->SetEventCallbacks(&callbacks, (jint) sizeof(callbacks));
        return AgentUtils::check_jvmti_error(jvmti, error,
                                             "JVMTI Callbacks registered successfully",
                                             "Cannot set JVMTI callbacks.");
    }

    int AgentUtils::register_jvmti_events(jvmtiEnv *jvmti) {
        jvmtiError error;
        int result;
        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_INIT, (jthread) NULL);
        result = AgentUtils::check_jvmti_error(jvmti, error,
                                      "Notifications for even VM_INIT set!",
                                      "Cannot set notifications for event VM_INIT");
        if(result == JNI_ERR){
            return result;
        }

        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_DEATH, (jthread) NULL);
        result = AgentUtils::check_jvmti_error(jvmti, error,
                                      "Notifications for even VM_DEATH set!",
                                      "Cannot set notifications for event VM_DEATH");
        if(result == JNI_ERR){
            return result;
        }

        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_START, (jthread) NULL);
        result = AgentUtils::check_jvmti_error(jvmti, error,
                                      "Notifications for even VM_START set!.",
                                      "Cannot set notifications for VM_START");
        if(result == JNI_ERR){
            return result;
        }

        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, (jthread) NULL);
        result = AgentUtils::check_jvmti_error(jvmti, error,
                                      "Notifications for even CLASS_FILE_LOAD_HOOK set!.",
                                      "Cannot set notification for event CLASS_FILE_LOAD_HOOK");
        if(result == JNI_ERR){
            return result;
        }
        logger->info() << "All JVMTI notifications successfully set";
        return JNI_OK;
    }

    int AgentUtils::create_JVMTI_env(JavaVM *jvm, jvmtiEnv *jvmti) {
        jint result = jvm->GetEnv((void **) &jvmti, JVMTI_VERSION_1_2);
        switch(result){
            case JNI_EVERSION:
                AgentUtils::logger->error() << "Obtaining JVMTI Env: JVMTI version " << JVMTI_VERSION_1_2 << " not supported. Is your J2SE a 1.5 or newer version?";
                return JNI_ERR;
            case JNI_OK:
                Agent::globalData->jvmti=jvmti;
                AgentUtils::logger->info() << "Obtaining JVMTI Env: JVMTI Env obtained successfully!";
                return JNI_OK;
            default:
                AgentUtils::logger->info() << "Obtaining JVMTI Env: Unknown error "<< result <<".";
                return JNI_ERR;
        }
    }

    int AgentUtils::JNI_AttachCurrentThread(JNIEnv *env) {
        jint result = Agent::globalData->jvm->GetEnv((void **) &env, JNI_VERSION_1_6);
        switch (result){
            case JNI_EDETACHED:
                AgentUtils::logger->info() << "Obtaining JNI Env: Current thread not attached to JVM, trying to attach.";
                if (AgentUtils::attachJNIToCurrentThread(Agent::globalData->jvm, env) == JNI_ERR) {
                    logger->error() << "Obtaining JNI Env: Failed to attach current thread to JVM. Terminating the agent!";
                    exit(JNI_ERR);
                }else{
                    logger->info() << "Obtaining JNI Env: Successfully attached current thread to JVM";
                    return DistraceAgent::JNI_ATTACHED_NOW;
                }
            case JNI_EVERSION:
                AgentUtils::logger->error() << "Obtaining JNI Env: JNI version " << JNI_VERSION_1_6 << " not supported. Terminating the agent!";
                exit(JNI_ERR);
            case JNI_OK:
                AgentUtils::logger->debug() << "Obtaining JNI Env: Current thread is already attached to the JVM!";
                return DistraceAgent::JNI_ALREADY_ATTACHED;
            default:
                AgentUtils::logger->info() << "Obtaining JNI Env: Unknown error "<< result <<". Terminating!";
                exit(result);
        }
    }

    void AgentUtils::JNI_DettachCurrentThread(int attachStatus){
        if(attachStatus == DistraceAgent::JNI_ATTACHED_NOW){
            Agent::globalData->jvm->DetachCurrentThread();
        }
    }

    int AgentUtils::attachJNIToCurrentThread(JavaVM* jvm, JNIEnv* jni){
        JavaVMAttachArgs args;
        args.version = JNI_VERSION_1_6; // choose your JNI version
        args.name = NULL; // you might want to give the java thread a name
        args.group = NULL; // you might want to assign the java thread to a ThreadGroup
        return jvm->AttachCurrentThread( (void**) &jni, &args);
    }

    int AgentUtils::init_agent(std::string options){
        std::map<std::string, std::string> args; // key = arg name, value = arg value
        if(Agent::parse_args(options, &args) == JNI_ERR){
            // stop the agent in case arguments are wrong
            return JNI_ERR;
        }

        if(Agent::init_instrumenter(args.find(Agent::ARG_INSTRUMENTOR_JAR)->second) == JNI_ERR){
            // stop the agent in case instrumenter JVM could not be started
            return JNI_ERR;
        }

        if (AgentUtils::create_JVMTI_env(Agent::globalData->jvm, Agent::globalData->jvmti) == JNI_ERR) {
            return JNI_ERR;
        };

        if(AgentUtils::register_jvmti_capabilities(Agent::globalData->jvmti) == JNI_ERR){
            return JNI_ERR;
        }

        if( AgentUtils::register_jvmti_callbacks(Agent::globalData->jvmti) == JNI_ERR){
            return JNI_ERR;
        }

        if( AgentUtils::register_jvmti_events(Agent::globalData->jvmti) == JNI_ERR){
            return JNI_ERR;
        }

        logger->info() << "Agent successfully initialized";
        return JNI_OK;
    }


    jobject AgentUtils::getInterceptorsClassLoader(JNIEnv* jni) {
        AgentUtils::logger->info() << "HEREHERE";
        std::string loader_class_name = "java/net/URLClassLoader";
        jclass loader_class = jni->FindClass(loader_class_name.c_str());
        AgentUtils::logger->info() << "HEREHERE";
        jmethodID  constructor = jni->GetMethodID(loader_class, "<init>", "([Ljava.net.URLClassLoader;)V");

        std::string url_class_name = "java/net/URL";
        jclass  url_class = jni->FindClass(url_class_name.c_str());
        jobjectArray arg = jni->NewObjectArray(1,url_class,NULL);
        jobject  loader_instance = jni->NewObject(loader_class,constructor,arg);
        Agent::globalData->jvm->DetachCurrentThread();
        return loader_instance;
    }

}
