//
// Created by Jakub Háva on 18/10/2016.
//

#include "ConstantPool.h"
#include "Constant.h"

using namespace Distrace;

ConstantPool::ConstantPool(ByteReader &reader) {
    this->reader = reader;
    int constant_pool_count = reader.readShort();
    constant_pool = new Constant[constant_pool_count];


    byte tag;
    /* constant_pool[0] is unused by the compiler and may be used freely
     * by the implementation.
     */
    for (int i = 1; i < constant_pool_count; i++) {
        constant_pool[i] = Constant::readConstant(reader);
        /* Quote from the JVM specification:
         * "All eight byte constants take up two spots in the constant pool.
         * If this is the n'th byte in the constant pool, then the next item
         * will be numbered n+2"
         *
         * Thus we have to increment the index counter.
         */
        tag = constant_pool[i].getTag();
        if ((tag == Const::CONSTANT_Double) || (tag == Const::CONSTANT_Long)) {
            i++;
        }
    }
}