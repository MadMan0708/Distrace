//
// Created by Jakub Háva on 18/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_BYTEREADER_H
#define DISTRACE_AGENT_CORE_BYTEREADER_H

#include "../Agent.h"

using namespace Distrace;

class ByteReader {

    public:
        /**
         * Create byte reader from provided byte array
         */
        ByteReader(const unsigned char *class_data, int class_data_len);
        int readInt();
        short readShort();
        byte readByte();
        float readFloat();
        long readLong();
        double readDouble();
        std::string readUTF();

        /**
         * Reads len bytes starting at the  current position
         */
        void readFully(byte *buff, int len);

        /**
         * Skip bytes
         */
        void skip(int howMany);

    private:
        const unsigned char *bytes;
        int bytesLen;
        int nextPos;
};



#endif //DISTRACE_AGENT_CORE_BYTEREADER_H
