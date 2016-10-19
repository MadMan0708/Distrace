//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTCLASS_H
#define DISTRACE_AGENT_CORE_CONSTANTCLASS_H


#include "Agent.h"
#include "Constant.h"

using namespace Distrace;

class ConstantClass : public Constant {

public:
    ConstantClass(short name_index);
    ConstantClass(ByteReader &reader);

private:
    int name_index;
};


#endif //DISTRACE_AGENT_CORE_CONSTANTCLASS_H
