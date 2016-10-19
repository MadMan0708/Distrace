//
// Created by Jakub Háva on 18/10/2016.
//

#include "Agent.h"

#ifndef DISTRACE_AGENT_CORE_JAVACONST_H
#define DISTRACE_AGENT_CORE_JAVACONST_H

#endif //DISTRACE_AGENT_CORE_JAVACONST_H

using namespace Distrace;
namespace JavaConst{
/** Marks a constant pool entry as type UTF-8.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.7">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_Utf8               = 1;

/** Marks a constant pool entry as type Integer.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.4">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_Integer            = 3;

/** Marks a constant pool entry as type Float.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.4">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_Float              = 4;

/** Marks a constant pool entry as type Long.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.5">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_Long               = 5;

/** Marks a constant pool entry as type Double.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.5">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_Double             = 6;

/** Marks a constant pool entry as a Class
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.1">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_Class              = 7;

/** Marks a constant pool entry as a Field Reference.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.2">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_Fieldref           = 9;

/** Marks a constant pool entry as type String
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.3">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_String             = 8;

/** Marks a constant pool entry as a Method Reference.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.2">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_Methodref          = 10;

/** Marks a constant pool entry as an Interface Method Reference.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.2">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_InterfaceMethodref = 11;

/** Marks a constant pool entry as a name and type.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.6">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_NameAndType        = 12;

/** Marks a constant pool entry as a Method Handle.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.8">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_MethodHandle       = 15;

/** Marks a constant pool entry as a Method Type.
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.9">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_MethodType         = 16;

/** Marks a constant pool entry as an Invoke Dynamic
 * @see  <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.10">
 * The Constant Pool in The Java Virtual Machine Specification</a> */
const byte CONSTANT_InvokeDynamic      = 18;

/** One of the access flags for fields, methods, or classes.
 *  @see #ACC_PUBLIC
 */
const short ACC_INTERFACE    = 0x0200;

/** One of the access flags for fields, methods, or classes.
 *  @see #ACC_PUBLIC
 */
const short ACC_ABSTRACT     = 0x0400;

/** One of the access flags for fields, methods, or classes.
 *  @see #ACC_PUBLIC
 */
const short ACC_FINAL        = 0x0010;

}