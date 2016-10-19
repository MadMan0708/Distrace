//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantMethodHandle.h"
#include "JavaConst.h"

ConstantMethodHandle::ConstantMethodHandle(ByteReader &reader) :ConstantMethodHandle(reader.readByte(), reader.readShort()){ }
ConstantMethodHandle::ConstantMethodHandle(int reference_kind, int reference_index) : Constant(JavaConst::CONSTANT_MethodHandle){
    this->reference_kind = reference_kind;
    this->reference_index = reference_index;
}
