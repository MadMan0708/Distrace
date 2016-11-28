//
// Created by Jakub Háva on 19/10/2016.
//

#include "Method.h"

Method::Method() {

}

Method::Method(ByteReader &reader, ConstantPool &constant_pool) : FieldOrMethod(reader, constant_pool) {

}
