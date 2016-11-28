//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTNAMEANDTYPE_H
#define DISTRACE_AGENT_CORE_CONSTANTNAMEANDTYPE_H


#include "../Agent.h"
#include "Constant.h"

using namespace Distrace;

class ConstantNameAndType: public Constant{

public:
    ConstantNameAndType(ByteReader &reader);
    ConstantNameAndType(int name_index, int signature_index);

private:
    int name_index; // Name of field/method
    int signature_index; // and its signature.
};


#endif //DISTRACE_AGENT_CORE_CONSTANTNAMEANDTYPE_H
