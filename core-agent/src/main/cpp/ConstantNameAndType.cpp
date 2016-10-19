//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantNameAndType.h"
#include "JavaConst.h"

ConstantNameAndType::ConstantNameAndType(ByteReader &reader) : ConstantNameAndType(reader.readShort(), reader.readShort()) { }
ConstantNameAndType::ConstantNameAndType(int name_index, int signature_index) : Constant(JavaConst::CONSTANT_NameAndType){
    this->name_index = name_index;
    this->signature_index = signature_index;
}
