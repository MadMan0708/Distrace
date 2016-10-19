//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CONSTANTUTF8_H
#define DISTRACE_AGENT_CORE_CONSTANTUTF8_H


#include "Constant.h"

class ConstantUtf8: public Constant {
public:

    ConstantUtf8(ByteReader &reader);
    ConstantUtf8(std::string bytes);


private:
    std::string bytes;
};


#endif //DISTRACE_AGENT_CORE_CONSTANTUTF8_H
