//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTPOOL_H
#define DISTRACE_AGENT_CORE_CONSTANTPOOL_H


#include "Agent.h"
#include "ByteReader.h"
#include "Constant.h"

using namespace Distrace;

class ConstantPool {
public:
    ConstantPool(ByteReader &reader);

private:
    ByteReader reader;
    Constant** constant_pool;
};


#endif //DISTRACE_AGENT_CORE_CONSTANTPOOL_H
