//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTLONG_H
#define DISTRACE_AGENT_CORE_CONSTANTLONG_H


#include "Constant.h"

using namespace Distrace;
class ConstantLong : public Constant {

public:

    ConstantLong(long bytes);
    ConstantLong(ByteReader &reader);

private:
    long bytes;

};


#endif //DISTRACE_AGENT_CORE_CONSTANTLONG_H
