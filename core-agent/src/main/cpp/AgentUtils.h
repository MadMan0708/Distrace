//
// Created by Jakub Háva on 08/04/16.
//
#include "jvmti.h"
#ifndef DISTRACE_AGENT_CORE_AGENTUTILS_H
#define DISTRACE_AGENT_CORE_AGENTUTILS_H

namespace DistraceAgent {
    class AgentUtils {
    public:
        static void check_jvmti_error(jvmtiEnv *env, jvmtiError error_number, const char *error_description);
    };
}


#endif //DISTRACE_AGENT_CORE_AGENTUTILS_H
