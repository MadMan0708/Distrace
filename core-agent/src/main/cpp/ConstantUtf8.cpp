//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantUtf8.h"
#include "JavaConst.h"

ConstantUtf8::ConstantUtf8(ByteReader &reader) : Constant(JavaConst::CONSTANT_Utf8) {
    this->bytes = reader.readUTF();
}

ConstantUtf8::ConstantUtf8(std::string bytes) :  Constant(JavaConst::CONSTANT_Utf8) {
    this->bytes = bytes;
}

std::string ConstantUtf8::getBytes() {
    return bytes;
}