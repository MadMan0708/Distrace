//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantInteger.h"
#include "JavaConst.h"

ConstantInteger::ConstantInteger(ByteReader &reader) : ConstantInteger(reader.readInt()){ }
ConstantInteger::ConstantInteger(int bytes) :Constant(JavaConst::CONSTANT_Integer){
    this->bytes = bytes;
}
