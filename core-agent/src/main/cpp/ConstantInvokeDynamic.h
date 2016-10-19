//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTINVOKEDYNAMIC_H
#define DISTRACE_AGENT_CORE_CONSTANTINVOKEDYNAMIC_H


#include "ConstantCP.h"

class ConstantInvokeDynamic: public ConstantCP {

public:

    ConstantInvokeDynamic(ByteReader &reader);
    ConstantInvokeDynamic(int boostrap_method_arr_index, int name_and_type_index);

};


#endif //DISTRACE_AGENT_CORE_CONSTANTINVOKEDYNAMIC_H
