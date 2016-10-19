//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantFloat.h"
#include "JavaConst.h"


ConstantFloat::ConstantFloat(ByteReader &reader) : ConstantFloat(reader.readFloat()){ }
ConstantFloat::ConstantFloat(float bytes) :Constant(JavaConst::CONSTANT_Float){
    this->bytes = bytes;
}
