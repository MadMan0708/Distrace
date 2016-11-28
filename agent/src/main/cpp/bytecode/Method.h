//
// Created by Jakub Háva on 19/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_METHOD_H
#define DISTRACE_AGENT_CORE_METHOD_H

#include "../Agent.h"
#include "FieldOrMethod.h"

using namespace Distrace;

class Method : public FieldOrMethod {

public:
    Method();
    Method(ByteReader &reader, ConstantPool &constant_pool);
};


#endif //DISTRACE_AGENT_CORE_METHOD_H
