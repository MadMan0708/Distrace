//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_BYTEREADER_H
#define DISTRACE_AGENT_CORE_BYTEREADER_H

#include "Agent.h"

using namespace Distrace;

class ByteReader {

    public:
        ByteReader(const unsigned char *class_data);
        int readInt();
        short readShort();
        byte readByte();
        float readFloat();

    private:
        const unsigned char *bytes;
        int nextPos;
};



#endif //DISTRACE_AGENT_CORE_BYTEREADER_H
