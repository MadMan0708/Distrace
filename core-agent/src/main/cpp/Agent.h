//
// Created by Jakub Háva on 08/04/16.
//
#include <map>
#include <string>
#include <vector>
#include <nnxx/socket.h>
#include <jvmti.h>
#include "InstrumentorAPI.h"
#include "AgentArgs.h"

#ifndef DISTRACE_AGENT_CORE_AGENTA_H
#define DISTRACE_AGENT_CORE_AGENTA_H

namespace Distrace {
    /**
     * Structure representing globally available data in the agent
     */
    typedef struct {
        /* JVMTI Environment */
        jvmtiEnv *jvmti;
        JavaVM *jvm;
        jboolean vm_started;
        jboolean vm_dead;
        InstrumentorAPI *inst_api;
        AgentArgs *agent_args; // key = arg name, value = arg value
    } GlobalAgentData;

    /**
     * This class represent main entry point to the agent.
     */
    class Agent {
    public:
        /**
         * Set log level and log whether the log level has been successfully set or not
         */
        static void set_log_level_and_log();

        /**
         * Get the AgentArgs from global data
         */
        static AgentArgs *getArgs();

        /**
         * Globally available data in the whole native agent
         */
        static GlobalAgentData *globalData;

        /**
         * Initialize global data with default values
         */
        static void init_global_data();
    };
}

#endif //DISTRACE_AGENT_CORE_AGENTA_H
