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

            // Send class name to the instrumentor and check if this class is available. Don't send the original
            // bytecode if it's already available, otherwise send it.
            if(Agent::globalData->inst_api->has_class(name)){
                log(LOGGER_AGENT_CALLBACKS)->debug() << "Instrumentor already has class: " << name;
            } else {
                log(LOGGER_AGENT_CALLBACKS)->info() << "Sending original bytecode to the instrumentor: " << name;
                // send bytecode for current class
                Agent::globalData->inst_api->send_byte_code(name, class_data, class_data_len);
            }

            //Agent::globalData->inst_api->add_sent_class(name);
            log(LOGGER_AGENT_CALLBACKS)->info() << "Send bytecode for all the dependencies for class: " << name;

            // load dependencies currently can't work since it works in deep-first search, but should be breadth first search

            // since right now it fails for this scenario
            // A -> B -> A
            //   -> C -> D -> E
            //  A is not loaded again, the recursion goes back and B is instrumented, however it requires all dependencies
            // for A also loaded  ( D and E ) and it fails with class not found exception

            // create a queue in the instrumentor map if it doesn't exist and enqueue current class

            // print value of the queue


            loadDependencies(env, loader, name, class_data, class_data_len);


            // ? Should the instrumentation be done only for the origin class ?
            // once we have all the dependencies in the instrumentor JVM, instrument the class

            //if( Agent::globalData->inst_api->is_root_name(name)){
                instrument(name, new_class_data, new_class_data_len, loader_name);
            //}
        }

        log(LOGGER_AGENT_CALLBACKS)->info() << "AFTER LOADING: The class " << name << " has been loaded by \""
        << loader_name << "\" class loader";
        AgentUtils::dettach_JNI_from_current_thread(attachStatus);
    }
}

void AgentCallbacks::loadDependencies(JNIEnv *env, jobject loader, const char *name, const unsigned char *class_data, jint class_data_len){
    //std::string name_to_process = Agent::globalData->inst_api->getClassToLoad();
    // queue current class
    // deque class, do class parsing and put all the the dependencies into queue

    std::vector<std::string> parsedTypes = ClassParser::parse(class_data, class_data_len);
    std::vector<std::string> filteredTypes = InstrumentorAPI::filterTypes(name, parsedTypes);

    // log obtained classes
    log(LOGGER_BYTECODE)->info() << "Parsing: " << name;
    for(std::vector<std::string>::iterator it=filteredTypes.begin() ; it <filteredTypes.end(); it++) {
        log(LOGGER_BYTECODE)->info() << "Found -> " << *it;
    }
    // load first class in the queue, then when a new class is about to be loaded( using on clas file load hook), load all its dependencies,
    // and dequeue it and put at the end of the queue

    for(std::vector<std::string>::iterator it=filteredTypes.begin() ; it < filteredTypes.end(); it++) {
        // load all dependencies for class
        std::string className = *it;
        className = JavaUtils::toNameWithDots(className);
        // check if this class has been already loaded
        // and if it was, don't send it again
        if(!Agent::globalData->inst_api->was_sent(className)) {
            Agent::globalData->inst_api->storeClassForLaterLoad(className);
            log(LOGGER_AGENT_CALLBACKS)->info() << "Adding type to a queue: " << className;
        }else{
            log(LOGGER_AGENT_CALLBACKS)->info() << "Skipping type: " << className << " for it was already in the cache";

        }
    }

   if(!Agent::globalData->inst_api->noClassToBeLoaded()){
       std::string classToLoad = Agent::globalData->inst_api->getClassToLoad();
       std::string toLoadWithDots = JavaUtils::toNameWithDots(classToLoad);

       log(LOGGER_AGENT_CALLBACKS)->info() << "Loading type: " << toLoadWithDots;
       JavaUtils::loadClass(env, loader, toLoadWithDots.c_str());
   }else{
       log(LOGGER_AGENT_CALLBACKS)->info() << "No more classes to be loaded, starting with the instrumenting back to the"
                                                      "original class";

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
    log(LOGGER_AGENT_CALLBACKS)->debug() << "Class: \"" << JavaUtils::getClassName(jni_env, klass) << "\" loaded";
}

void JNICALL AgentCallbacks::cbClassPrepare(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread, jclass klass) {
    log(LOGGER_AGENT_CALLBACKS)->debug() << "Class: \"" << JavaUtils::getClassName(jni_env, klass) << "\" prepared";


}