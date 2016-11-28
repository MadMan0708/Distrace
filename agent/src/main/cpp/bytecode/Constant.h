//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANT_H
#define DISTRACE_AGENT_CORE_CONSTANT_H

#include "../Agent.h"
#include "../utils/ByteReader.h"

using namespace Distrace;

class Constant {

public:
    static Constant* readConstant(ByteReader &reader);
    Constant(byte tag);
    Constant();
    byte getTag();
private:
    byte tag;



};


#endif //DISTRACE_AGENT_CORE_CONSTANT_H
