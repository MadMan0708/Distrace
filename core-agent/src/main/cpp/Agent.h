//
// Created by Jakub Háva on 08/04/16.
//
#include <map>
#include <string>
#include <vector>

#ifndef DISTRACE_AGENT_CORE_AGENTA_H
#define DISTRACE_AGENT_CORE_AGENTA_H

namespace DistraceAgent {
    typedef struct {
        /* JVMTI Environment */
        jvmtiEnv *jvmti = NULL;
        JNIEnv *jni = NULL;
        JavaVM *jvm = NULL;
        jboolean vm_started = (jboolean) false;
        jboolean vm_dead = (jboolean) false;
    } GlobalAgentData;

    class Agent {
    public:
        static GlobalAgentData* globalData;
        static void init_global_data();
        static int parse_args(std::string options, std::map<std::string, std::string> *args);
        static int init_instrumenter(std::string path_to_jar);
        static const std::string ARG_INSTRUMENTOR_JAR;
        static std::vector<std::string> internal_classes_to_instrument;
        /**
         * This method lists the classes which are needed to be instrumented by purposes of this library
         * The list of classes is sorted in order to ensure faster lookup ( using binary_search )
         */
        static std::vector<std::string> init_list_of_classes_to_instrument();
    };
}

#endif //DISTRACE_AGENT_CORE_AGENTA_H
