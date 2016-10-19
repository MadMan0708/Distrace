//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantString.h"
#include "JavaConst.h"

using namespace Distrace;

ConstantString::ConstantString(ByteReader &reader): ConstantString(reader.readShort()) { }
ConstantString::ConstantString(int string_index) :Constant(JavaConst::CONSTANT_String){
    this->string_index = string_index;
}
