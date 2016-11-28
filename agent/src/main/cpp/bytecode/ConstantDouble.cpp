//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantDouble.h"
#include "JavaConst.h"

ConstantDouble::ConstantDouble(ByteReader &reader) : ConstantDouble(reader.readDouble()){ }
ConstantDouble::ConstantDouble(double bytes) :Constant(JavaConst::CONSTANT_Double){
    this->bytes = bytes;
}
