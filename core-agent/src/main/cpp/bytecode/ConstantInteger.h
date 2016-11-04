//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTINTEGER_H
#define DISTRACE_AGENT_CORE_CONSTANTINTEGER_H

#include "../Agent.h"
#include "../utils/ByteReader.h"
#include "Constant.h"

using namespace Distrace;

class ConstantInteger: public Constant {

public:

    ConstantInteger(int bytes);
    ConstantInteger(ByteReader &reader);

private:
    int bytes;
};


#endif //DISTRACE_AGENT_CORE_CONSTANTINTEGER_H
