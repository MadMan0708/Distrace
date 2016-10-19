//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTSTRING_H
#define DISTRACE_AGENT_CORE_CONSTANTSTRING_H

#include "Agent.h"
#include "Constant.h"


using namespace Distrace;

class ConstantString : public Constant{

public:
    ConstantString(ByteReader &reader);
    ConstantString(int string_index);

private:
    int string_index;
};


#endif //DISTRACE_AGENT_CORE_CONSTANTSTRING_H
