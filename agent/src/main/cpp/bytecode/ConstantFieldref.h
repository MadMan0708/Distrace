//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTFIELDREF_H
#define DISTRACE_AGENT_CORE_CONSTANTFIELDREF_H

#include "../Agent.h"
#include "../utils/ByteReader.h"
#include "ConstantCP.h"

using namespace Distrace;

class ConstantFieldref: public ConstantCP{

public:

    ConstantFieldref(ByteReader &reader);
    ConstantFieldref(int class_index, int name_and_type_index);

};


#endif //DISTRACE_AGENT_CORE_CONSTANTFIELDREF_H
