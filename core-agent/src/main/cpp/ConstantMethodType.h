//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTMETHODTYPE_H
#define DISTRACE_AGENT_CORE_CONSTANTMETHODTYPE_H

#include "Agent.h"
#include "ByteReader.h"
#include "Constant.h"

using namespace Distrace;
class ConstantMethodType : public Constant{
public:

    ConstantMethodType(ByteReader &reader);
    ConstantMethodType(int descriptor_index);

private:
    int descriptor_index;
};


#endif //DISTRACE_AGENT_CORE_CONSTANTMETHODTYPE_H
