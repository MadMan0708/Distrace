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
        jboolean vmStarted;
        jboolean vmDead;
        InstrumentorAPI *instApi;
        AgentArgs *args; // key = arg name, value = arg value
        /* Data access Lock */
        jrawMonitorID  lock;
    } GlobalAgentData;

    /**
     * This class represents main entry point to the agent.
     */
    class Agent {
    public:

        /**
         * Get instrumentor api from the global data
         */
        static InstrumentorAPI *getInstApi();
        /**
         * Get arguments passed to the agent from global data
         */
        static AgentArgs *getArgs();

        /**
         * Get globally available data in the whole native agent
         */
        static GlobalAgentData *globalData;

        /**
         * Initialize global data with default values
         */
        static void initGlobalData();
    };
}

#endif //DISTRACE_AGENT_CORE_AGENTA_H
