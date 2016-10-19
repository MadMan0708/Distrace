//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTFLOAT_H
#define DISTRACE_AGENT_CORE_CONSTANTFLOAT_H


#include "Constant.h"

class ConstantFloat: public Constant {
public:

    ConstantFloat(float bytes);
    ConstantFloat(ByteReader &reader);

private:
    float bytes;
};


#endif //DISTRACE_AGENT_CORE_CONSTANTFLOAT_H
