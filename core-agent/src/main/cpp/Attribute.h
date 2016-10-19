//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_ATTRIBUTE_H
#define DISTRACE_AGENT_CORE_ATTRIBUTE_H


#include "ConstantPool.h"

class Attribute {

public:
     Attribute();
     Attribute(byte tag, int name_index, int length, ConstantPool &constant_pool);

private:
    int name_index; // Points to attribute name in constant pool
    int length; // Content length of attribute field
    byte tag; // Tag to distinguish subclasses
    ConstantPool* constant_pool;

};


#endif //DISTRACE_AGENT_CORE_ATTRIBUTE_H
