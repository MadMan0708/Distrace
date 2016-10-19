//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantMethodType.h"
#include "JavaConst.h"

ConstantMethodType::ConstantMethodType(ByteReader &reader) : ConstantMethodType(reader.readShort()) { }

ConstantMethodType::ConstantMethodType(int descriptor_index) : Constant(JavaConst::CONSTANT_MethodType) {
    this->descriptor_index = descriptor_index;
}
