//
// Created by Jakub Háva on 18/10/2016.
//

#include "Constant.h"
#include "JavaConst.h"
#include "ConstantClass.h"
#include "ConstantFieldref.h"
#include "ConstantMethodref.h"
#include "ConstantInterfaceMethodref.h"
#include "ConstantString.h"
#include "ConstantInteger.h"
#include "ConstantFloat.h"
#include "ConstantLong.h"
#include "ConstantDouble.h"
#include "ConstantNameAndType.h"
#include "ConstantUtf8.h"
#include "ConstantMethodHandle.h"
#include "ConstantMethodType.h"
#include "ConstantInvokeDynamic.h"

using namespace Distrace;

Constant::Constant() {}
Constant::Constant(byte tag) {
    this->tag = tag;
}

byte Constant::getTag() {
    return tag;
}

Constant* Constant::readConstant(ByteReader &reader) {
    byte b = reader.readByte(); // Read tag byte
    switch (b) {
        case JavaConst::CONSTANT_Class:
            return new ConstantClass(reader);
        case JavaConst::CONSTANT_Fieldref:
            return new ConstantFieldref(reader);
        case JavaConst::CONSTANT_Methodref:
            return new ConstantMethodref(reader);
        case JavaConst::CONSTANT_InterfaceMethodref:
            return new ConstantInterfaceMethodref(reader);
        case JavaConst::CONSTANT_String:
            return new ConstantString(reader);
        case JavaConst::CONSTANT_Integer:
            return new ConstantInteger(reader);
        case JavaConst::CONSTANT_Float:
            return new ConstantFloat(reader);
        case JavaConst::CONSTANT_Long:
            return new ConstantLong(reader);
        case JavaConst::CONSTANT_Double:
            return new ConstantDouble(reader);
        case JavaConst::CONSTANT_NameAndType:
            return new ConstantNameAndType(reader);
        case JavaConst::CONSTANT_Utf8:
            return new ConstantUtf8(reader);
        case JavaConst::CONSTANT_MethodHandle:
            return new ConstantMethodHandle(reader);
        case JavaConst::CONSTANT_MethodType:
            return new ConstantMethodType(reader);
        case JavaConst::CONSTANT_InvokeDynamic:
            return new ConstantInvokeDynamic(reader);
        default:
            throw "Invalid byte tag in constant pool: " + b;
    }
}