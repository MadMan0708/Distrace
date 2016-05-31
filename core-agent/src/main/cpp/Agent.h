//
// Created by Jakub Háva on 08/04/16.
//
#include <map>
#include <string>
#include <vector>
#include <nnxx/socket.h>
#include <jvmti.h>
#include "InstrumentorAPI.h"

#ifndef DISTRACE_AGENT_CORE_AGENTA_H
#define DISTRACE_AGENT_CORE_AGENTA_H

namespace Distrace {
    static int INSTRUMENTOR_INSTRUMENT = 1;
    static int INSTRUMENTOR_NO_INSTRUMENT = 2;
    typedef struct {
        /* JVMTI Environment */
        jvmtiEnv *jvmti;
        JavaVM *jvm;
        jboolean vm_started;
        jboolean vm_dead;
        InstrumentorAPI *inst_api;
        std::map<std::string, std::string> agent_args; // key = arg name, value = arg value
    } GlobalAgentData;

    class Agent {
    public:
        static GlobalAgentData* globalData;
        static void init_global_data();
        /**
         * Parses arguments and fill the agent_args map in the globalData
         */
        static int parse_args(std::string options, std::map<std::string, std::string> &args);
        static const std::string ARG_INSTRUMENTOR_JAR;
        static const std::string ARG_LOG_LEVEL;
        static const std::string ARG_LOG_DIR;
        static const std::string ARG_SOCKET_ADDRESS;
        static std::string get_arg_value(std::string arg_name);
    };
}

#endif //DISTRACE_AGENT_CORE_AGENTA_H
