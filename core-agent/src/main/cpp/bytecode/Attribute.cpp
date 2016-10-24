//
// Created by Jakub Háva on 18/10/2016.
//

#include "Attribute.h"
#include "JavaConst.h"
#include "ConstantUtf8.h"

Attribute::Attribute() { }
Attribute::Attribute(byte tag, int name_index, int length, ConstantPool &constant_pool){
    this->tag = tag;
    this->name_index = name_index;
    this->length = length;
    this->constant_pool = &constant_pool;
}

void Attribute::readAttribute(ByteReader &reader, ConstantPool &constant_pool) {
    byte tag = JavaConst::ATTR_UNKNOWN; // Unknown attribute
    // Get class name from constant pool via `name_index' indirection
    int name_index = reader.readShort();
    ConstantUtf8 *c = (ConstantUtf8*) constant_pool.getConstant(name_index, JavaConst::CONSTANT_Utf8);
    std::string name = c->getBytes();

    // Length of data in bytes
    int length = reader.readInt();

    // Compare strings to find known attribute
    for (byte i = 0; i < JavaConst::KNOWN_ATTRIBUTES; i++)
    {
        if (name == JavaConst::getAttributeName(i)) {
            tag = i; // found!
            break;
        }
    }

    // we are not interested in Attributes at this moment, SKIP THEM
    reader.skip(length);

}

