//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantCP.h"

using namespace Distrace;

ConstantCP::ConstantCP(byte tag, ByteReader &reader): ConstantCP(tag, reader.readShort(), reader.readShort()) { }

ConstantCP::ConstantCP(byte tag, int class_index, int name_and_type_index) : Constant(tag) {
    this->class_index = class_index;
    this->name_and_type_index = name_and_type_index;
}
