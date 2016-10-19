//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTINTERFACEMETHODREF_H
#define DISTRACE_AGENT_CORE_CONSTANTINTERFACEMETHODREF_H


#include "Agent.h"
#include "ByteReader.h"
#include "ConstantCP.h"

using namespace Distrace;

class ConstantInterfaceMethodref : public ConstantCP{

public:
    ConstantInterfaceMethodref(ByteReader &reader);
    ConstantInterfaceMethodref(int class_index, int name_and_type_index);
};


#endif //DISTRACE_AGENT_CORE_CONSTANTINTERFACEMETHODREF_H
