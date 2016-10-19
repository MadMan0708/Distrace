//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_FIELD_H
#define DISTRACE_AGENT_CORE_FIELD_H

#include "Agent.h"
#include "ByteReader.h"
#include "ConstantPool.h"
#include "Attribute.h"
#include "FieldOrMethod.h"

using namespace Distrace;

class Field : public FieldOrMethod {

public:
    Field();
    Field(ByteReader &reader, ConstantPool &constant_pool);
    Field(int access_flags, int name_index, int signature_index, Attribute attributes[], ConstantPool &constant_pool);

};


#endif //DISTRACE_AGENT_CORE_FIELD_H
