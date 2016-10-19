//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTMETHODHANDLE_H
#define DISTRACE_AGENT_CORE_CONSTANTMETHODHANDLE_H

#include "Agent.h"
#include "Constant.h"

using namespace Distrace;

class ConstantMethodHandle: public Constant{

public:

    ConstantMethodHandle(ByteReader &reader);
    ConstantMethodHandle(int reference_kind, int reference_index);

private:
int reference_kind;
int reference_index;

};


#endif //DISTRACE_AGENT_CORE_CONSTANTMETHODHANDLE_H
