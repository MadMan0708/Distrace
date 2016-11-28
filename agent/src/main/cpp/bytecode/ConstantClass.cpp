//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantClass.h"
#include "JavaConst.h"

ConstantClass::ConstantClass(ByteReader &reader) : ConstantClass(reader.readShort()){

}

ConstantClass::ConstantClass(short name_index) : Constant(JavaConst::CONSTANT_Class) {
    this->name_index = name_index;
}

int ConstantClass::getNameIndex() {
    return this->name_index;
}

