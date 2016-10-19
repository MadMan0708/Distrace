//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantMethodref.h"
#include "JavaConst.h"

using namespace Distrace;
ConstantMethodref::ConstantMethodref(ByteReader &reader): ConstantCP(JavaConst::CONSTANT_Methodref, reader) { }
ConstantMethodref::ConstantMethodref(int class_index, int name_and_type_index) : ConstantCP(JavaConst::CONSTANT_Methodref, class_index, name_and_type_index){ }