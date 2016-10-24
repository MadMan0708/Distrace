//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_BYTEREADER_H
#define DISTRACE_AGENT_CORE_BYTEREADER_H

#include "Agent.h"

using namespace Distrace;

class ByteReader {

    public:
        ByteReader(const unsigned char *class_data, int class_data_len);
        int readInt();
        short readShort();
        byte readByte();
        float readFloat();
        long readLong();
        double readDouble();
        std::string readUTF();
        void readFully(byte* buff, int len);
        void skip(int howMany);

    private:
        const unsigned char *bytes;
        int bytes_len;
        int nextPos;
};



#endif //DISTRACE_AGENT_CORE_BYTEREADER_H
