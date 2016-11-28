//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantInvokeDynamic.h"
#include "JavaConst.h"

ConstantInvokeDynamic::ConstantInvokeDynamic(ByteReader &reader): ConstantInvokeDynamic(reader.readShort(), reader.readShort()) { }
ConstantInvokeDynamic::ConstantInvokeDynamic(int boostrap_method_arr_index, int name_and_type_index) : ConstantCP(JavaConst::CONSTANT_InvokeDynamic, boostrap_method_arr_index, name_and_type_index) { }

