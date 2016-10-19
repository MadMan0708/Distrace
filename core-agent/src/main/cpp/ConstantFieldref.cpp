//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantFieldref.h"
#include "JavaConst.h"
#include "Constant.h"

using namespace Distrace;


ConstantFieldref::ConstantFieldref(ByteReader &reader) : ConstantCP(JavaConst::CONSTANT_Fieldref, reader) { }
ConstantFieldref::ConstantFieldref(int class_index, int name_and_type_index) : ConstantCP(JavaConst::CONSTANT_Fieldref, class_index, name_and_type_index) { }
