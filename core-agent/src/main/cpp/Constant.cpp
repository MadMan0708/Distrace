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

using namespace Distrace;

Constant::Constant() {}
Constant::Constant(byte tag) {
    this->tag = tag;
}

Constant Constant::readConstant(ByteReader &reader) {
    byte b = reader.readByte(); // Read tag byte
    switch (b) {
        case JavaConst::CONSTANT_Class:
            return ConstantClass(reader);
        case JavaConst::CONSTANT_Fieldref:
            return ConstantFieldref(reader);
        case JavaConst::CONSTANT_Methodref:
            return ConstantMethodref(reader);
        case JavaConst::CONSTANT_InterfaceMethodref:
            return ConstantInterfaceMethodref(reader);
        case JavaConst::CONSTANT_String:
            return ConstantString(reader);
        case JavaConst::CONSTANT_Integer:
            return ConstantInteger(reader);
        case JavaConst::CONSTANT_Float:
            return ConstantFloat(reader);
        case JavaConst::CONSTANT_Long:
            return ConstantLong(reader);
        case JavaConst::CONSTANT_Double:
            return ConstantDouble(reader);
        case JavaConst::CONSTANT_NameAndType:
            return ConstantNameAndType(reader);
        case JavaConst::CONSTANT_Utf8:
            return ConstantUtf8(reader);
        case JavaConst::CONSTANT_MethodHandle:
            return new ConstantMethodHandle(input);
        case JavaConst::CONSTANT_MethodType:
            return new ConstantMethodType(input);
        case JavaConst::CONSTANT_InvokeDynamic:
            return new ConstantInvokeDynamic(input);
        default:
            throw "Invalid byte tag in constant pool: " + b;
    }
}