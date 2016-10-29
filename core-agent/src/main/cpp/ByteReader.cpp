//
// Created by Jakub Háva on 18/10/2016.
//

#include "ByteReader.h"

using namespace Distrace;

ByteReader::ByteReader(const unsigned char *class_data, int data_len) {
    bytes = class_data;
    bytes_len = data_len;
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

long ByteReader::readLong() {
    int ret = (bytes[nextPos] << 56) | (bytes[nextPos+1] << 48) | (bytes[nextPos+2] << 40) | (bytes[nextPos+3] << 32) |
              (bytes[nextPos+4] << 24) | (bytes[nextPos+5] << 16) | (bytes[nextPos+6] << 8) | bytes[nextPos+7];
    nextPos = nextPos + 8;
    return ret;
}

// Same as Float.intBitsToFloat
float ByteReader::readFloat() {
    union  {
        int integer_bits;
        float converted_float_bits;
    } bits;

    bits.integer_bits = readInt();
    return bits.converted_float_bits;
}

double ByteReader::readDouble() {
    union  {
        long integer_bits;
        double converted_double_bits;
    } bits;
    bits.integer_bits = readLong();
    return bits.converted_double_bits;
}

void ByteReader::readFully(byte* buf, int len){
    for(int i=0;i<len;i++){
        buf[i] = readByte();
    }
}

std::string ByteReader::readUTF(){
    short utflen = readShort();
    byte* bytearr = new byte[utflen];
    char chararr[utflen];


        int c, char2, char3;
        int count = 0;
        int chararr_count=0;

        readFully(bytearr, utflen);
        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) break;
            count++;
            chararr[chararr_count++]=(char)c;
        }
        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                    /* 0xxxxxxx*/
                    count++;
                    chararr[chararr_count++]=(char)c;
                    break;
                case 12: case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        throw "malformed input: partial character at end";
                    char2 = (int) bytearr[count-1];
                    if ((char2 & 0xC0) != 0x80)
                        throw "malformed input around byte " + count;
                    chararr[chararr_count++]=(char)(((c & 0x1F) << 6) |
                                                    (char2 & 0x3F));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen) {
                        throw "malformed input: partial character at end";
                    }

                    char2 = (int) bytearr[count-2];
                    char3 = (int) bytearr[count-1];

                    std::cout << "char 2 " << char2 << " char3 " << char3 << " "<< +((char2 & 0xC0) != 0x80) << +((char3 & 0xC0) != 0x80) <<std::endl;

                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                        throw "malformed input around byte " + (count - 1);
                    }
                    chararr[chararr_count++]=(char)(((c     & 0x0F) << 12) |
                                                    ((char2 & 0x3F) << 6)  |
                                                    ((char3 & 0x3F) << 0));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    throw "malformed input around byte " + count;
            }
        }

    return std::string(chararr, 0, chararr_count);
}

void ByteReader::skip(int howMany) {
    nextPos+=howMany;
}

