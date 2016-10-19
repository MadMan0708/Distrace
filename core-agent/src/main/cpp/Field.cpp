//
// Created by Jakub Háva on 18/10/2016.
//

#include "Field.h"


Field::Field(ByteReader &reader, ConstantPool &constant_pool): FieldOrMethod(reader, constant_pool) { }
Field::Field(){}
