//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTCP_H
#define DISTRACE_AGENT_CORE_CONSTANTCP_H

#include "Agent.h"
#include "Constant.h"

using namespace Distrace;

class ConstantCP: public Constant {

public:

    ConstantCP(byte tag, ByteReader &reader);
    ConstantCP(byte tag, int class_index, int name_and_type_index);

private:
    int class_index;
    int name_and_type_index;



};


#endif //DISTRACE_AGENT_CORE_CONSTANTCP_H
