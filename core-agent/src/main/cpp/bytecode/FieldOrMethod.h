//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_FIELDORMETHOD_H
#define DISTRACE_AGENT_CORE_FIELDORMETHOD_H


#include "../ByteReader.h"
#include "ConstantPool.h"
#include "Attribute.h"
#include "../AccessFlags.h"

class FieldOrMethod: public AccessFlags {


public:
    FieldOrMethod();
    FieldOrMethod(ByteReader &reader, ConstantPool &constant_pool);
    std::string getSignature();
private:


int name_index; // Points to field name in constant pool
int signature_index; // Points to encoded signature
Attribute* attributes; // Collection of attributes
int attributes_count; // No. of attributes
    ConstantPool* constant_pool;
};


#endif //DISTRACE_AGENT_CORE_FIELDORMETHOD_H
