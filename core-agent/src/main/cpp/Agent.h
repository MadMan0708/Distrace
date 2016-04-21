//
// Created by Jakub Háva on 08/04/16.
//
#include <map>
#include <string>

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
        static int parse_args(std::string options, std::map<std::string, std::string> args);
        static const std::string ARG_INSTRUMENTOR_JAR;
    };
}

#endif //DISTRACE_AGENT_CORE_AGENTA_H
