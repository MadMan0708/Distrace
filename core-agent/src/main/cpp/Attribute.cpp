//
// Created by Jakub Háva on 18/10/2016.
//

#include "Attribute.h"

Attribute::Attribute() { }
Attribute::Attribute(byte tag, int name_index, int length, ConstantPool &constant_pool){
    this->tag = tag;
    this->name_index = name_index;
    this->length = length;
    this->constant_pool = &constant_pool;
}