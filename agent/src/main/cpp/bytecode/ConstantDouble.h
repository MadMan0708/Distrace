//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTDOUBLE_H
#define DISTRACE_AGENT_CORE_CONSTANTDOUBLE_H


#include "Constant.h"

class ConstantDouble: public Constant {
public:

    ConstantDouble(double bytes);
    ConstantDouble(ByteReader &reader);

private:
    double bytes;
};


#endif //DISTRACE_AGENT_CORE_CONSTANTDOUBLE_H
