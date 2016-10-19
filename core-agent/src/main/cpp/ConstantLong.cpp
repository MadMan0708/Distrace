//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantLong.h"
#include "JavaConst.h"

ConstantLong::ConstantLong(ByteReader &reader) : ConstantLong(reader.readLong()){ }
ConstantLong::ConstantLong(long bytes) :Constant(JavaConst::CONSTANT_Long){
    this->bytes = bytes;
}
