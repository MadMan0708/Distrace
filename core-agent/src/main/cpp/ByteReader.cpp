//
// Created by Jakub Háva on 18/10/2016.
//

#include "ByteReader.h"
#include "Agent.h"

using namespace Distrace;

union int_to_float_bits {
    int32_t integer_bits;
    float converted_float_bits;
};


ByteReader::ByteReader(const unsigned char *class_data) {
    bytes = class_data;
    nextPos = 0;
}

int ByteReader::readInt() {
    int ret = (bytes[nextPos] << 24) | (bytes[nextPos+1] << 16) | (bytes[nextPos+2] << 8) | bytes[nextPos+3];
    nextPos = nextPos + 4;
    return ret;

}

short ByteReader::readShort() {
    short ret = (bytes[nextPos] << 8) | bytes[nextPos+1];
    nextPos = nextPos + 2;
    return ret;
}

byte ByteReader::readByte() {
    byte ret = bytes[nextPos];
    nextPos = nextPos + 1;
    return ret;
}

// Same as Float.intBitsToFloat
float ByteReader::readFloat() {
    union int_to_float_bits bits;
    bits.integer_bits = readInt();
    return bits.converted_float_bits;
}