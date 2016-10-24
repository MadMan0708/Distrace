//
// Created by Jakub Háva on 18/10/2016.
//

#include <string>

namespace Distrace{
    namespace JavaConst{

        std::string CONSTANT_NAMES[] = {
                "", "CONSTANT_Utf8", "", "CONSTANT_Integer",
                "CONSTANT_Float", "CONSTANT_Long", "CONSTANT_Double",
                "CONSTANT_Class", "CONSTANT_String", "CONSTANT_Fieldref",
                "CONSTANT_Methodref", "CONSTANT_InterfaceMethodref",
                "CONSTANT_NameAndType", "", "", "CONSTANT_MethodHandle",
                "CONSTANT_MethodType", "", "CONSTANT_InvokeDynamic" };


         std::string getConstantName(int index) {
            return CONSTANT_NAMES[index];
        }

        std::string ATTRIBUTE_NAMES[] = {
                "SourceFile", "ConstantValue", "Code", "Exceptions",
                "LineNumberTable", "LocalVariableTable",
                "InnerClasses", "Synthetic", "Deprecated",
                "PMGClass", "Signature", "StackMap",
                "RuntimeVisibleAnnotations", "RuntimeInvisibleAnnotations",
                "RuntimeVisibleParameterAnnotations", "RuntimeInvisibleParameterAnnotations",
                "AnnotationDefault", "LocalVariableTypeTable", "EnclosingMethod", "StackMapTable",
                "BootstrapMethods", "MethodParameters"
        };


        std::string getAttributeName(int index){
            return ATTRIBUTE_NAMES[index];
        }
    }
}