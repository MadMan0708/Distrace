//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTMETHODREF_H
#define DISTRACE_AGENT_CORE_CONSTANTMETHODREF_H

#include "Agent.h"
#include "ConstantCP.h"

using namespace Distrace;

class ConstantMethodref: public ConstantCP {

public:
    ConstantMethodref(ByteReader &reader);
    ConstantMethodref(int class_index, int name_and_type_index);
};


#endif //DISTRACE_AGENT_CORE_CONSTANTMETHODREF_H
