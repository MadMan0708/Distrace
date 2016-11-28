//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantInterfaceMethodref.h"
#include "ConstantCP.h"
#include "JavaConst.h"

using namespace Distrace;

ConstantInterfaceMethodref::ConstantInterfaceMethodref(ByteReader &reader) : ConstantCP(JavaConst::CONSTANT_InterfaceMethodref, reader) { }
ConstantInterfaceMethodref::ConstantInterfaceMethodref(int class_index, int name_and_type_index) : ConstantCP(JavaConst::CONSTANT_InterfaceMethodref, class_index, name_and_type_index) { }
