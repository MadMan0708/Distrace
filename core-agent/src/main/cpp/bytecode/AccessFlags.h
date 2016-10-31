//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_ACCESSFLAGS_H
#define DISTRACE_AGENT_CORE_ACCESSFLAGS_H

#include "../Agent.h"

using namespace Distrace;
class AccessFlags {
public:
    AccessFlags();
AccessFlags(int a);

private:
    int access_flags;
};


#endif //DISTRACE_AGENT_CORE_ACCESSFLAGS_H
