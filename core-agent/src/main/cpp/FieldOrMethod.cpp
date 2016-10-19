//
// Created by Jakub Háva on 18/10/2016.
//

#include "FieldOrMethod.h"
#include "AccessFlags.h"
#include "JavaConst.h"
#include "ConstantUtf8.h"

FieldOrMethod::FieldOrMethod() { }
FieldOrMethod::FieldOrMethod(ByteReader &reader, ConstantPool &constant_pool) : AccessFlags(reader.readShort()) {
    name_index = reader.readShort();
    signature_index = reader.readShort();
    attributes_count = reader.readShort();
    this->constant_pool = &constant_pool;

    attributes = new Attribute[attributes_count];
    //TODO: Attribute handling
    //for (int i = 0; i < attributes_count; i++) {
    //    attributes[i] = Attribute.readAttribute(reader, constant_pool);
   // }
}


std::string FieldOrMethod::getSignature() {
    ConstantUtf8 *c = (ConstantUtf8*)this->constant_pool->getConstant(signature_index, JavaConst::CONSTANT_Utf8);
    return c->getBytes();
}