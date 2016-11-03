//
// Created by Jakub Háva on 24/10/2016.
//

#include <boost/algorithm/string.hpp>
#include "ClassParser.h"
#include "JavaConst.h"
#include "../JavaUtils.h"


using namespace Logging;

ClassParser::ClassParser(ByteReader &reader) : reader(reader) {
}


void ClassParser::readMagicId(){
    int magicId = reader.readInt();
    int JVM_CLASSFILE_MAGIC = 0xCAFEBABE;
    if(JVM_CLASSFILE_MAGIC != magicId){
        throw "Magic id is not correct";
    }
}

void ClassParser::readVersions(){
    minorVersion = reader.readShort();
    majorVersion = reader.readShort();
}


void ClassParser::readConstantPool(){
    constantPool = new ConstantPool(reader);
}


void ClassParser::readClassInfo(){
    int access_flags = reader.readShort();
    /* Interfaces are implicitly abstract, the flag should be set
     * according to the JVM specification.
     */
    if ((access_flags & JavaConst::ACC_INTERFACE) != 0) {
        access_flags |= JavaConst::ACC_ABSTRACT;
    }
    if (((access_flags & JavaConst::ACC_ABSTRACT) != 0)
        && ((access_flags & JavaConst::ACC_FINAL) != 0)) {
        throw "Class can't be both final and abstract";
    }
    classNameIndex = reader.readShort();
    superClassNameIndex = reader.readShort();
}

void ClassParser::readInterfaces(){
    numInterfaces = reader.readShort();
    interfaces = new int[numInterfaces];
    // there can't be duplicate interfaces in a class, se we can store them without looking if the interface is
    // already stored
    for (int i = 0; i < numInterfaces; i++) {
        interfaces[i] = reader.readShort();
        std::string interfaceName = constantPool->getConstantString(interfaces[i], JavaConst::CONSTANT_Class);
        interfacesRefs.push_back(interfaceName);
    }
}


std::string ClassParser::classNameFromSignature(std::string typeSignature){
    if(boost::starts_with(typeSignature, "L")){
        std::string trimmed = typeSignature.substr(1, typeSignature.length()-2); // remove leading L and ; and the end
        return trimmed;
    }else{
        return "";
    }
}

void ClassParser::saveRefUniquely(std::string ref, std::vector<std::string> &where){
    // we are only interested in references which does not belong to java package
    if(!ref.empty()){
        if (std::find(where.begin(), where.end(), ref) == where.end()) {
            // put the ref to the list of unique refs to be loaded
            where.push_back(ref);
        }
    }
}


void ClassParser::readFields(){
    numFields = reader.readShort();

    fields = new Field[numFields];
    for (int i = 0; i < numFields; i++) {
        fields[i] = *(new Field(reader, *constantPool));
        std::string signature = fields[i].getSignature();
        std::string ref = classNameFromSignature(signature);
        saveRefUniquely(ref, fieldRefs);
    }
}

std::string ClassParser::returnValueFromSignature(std::string methodSignature){
    // process return value
    std::string typeSignature = methodSignature.substr(methodSignature.find(")")+1);
    return classNameFromSignature(typeSignature);
}

void ClassParser::parseAndSaveArguments(std::string ref){
    std::string arguments = ref.substr(1, ref.find(")")-1);
    while(arguments.find("L") != std::string::npos){
        unsigned long start = arguments.find("L");

        std::string trimmedStart = arguments.substr(start);

        unsigned long end = (trimmedStart.find(";") + 1);

        std::string argSignature = trimmedStart.substr(0, end);

        std::string parsedRef = classNameFromSignature(argSignature);
        saveRefUniquely(parsedRef, methodRefs);
        arguments = trimmedStart.substr(end);

    }
}

void ClassParser::readMethods(){
    numMethods = reader.readShort();

    methods = new Method[numMethods];
    for (int i = 0; i < numMethods; i++) {
        methods[i] = (*new Method(reader, *constantPool));
        std::string methodSignature = methods[i].getSignature();

        std::string parsedRef = returnValueFromSignature(methodSignature);
        saveRefUniquely(parsedRef, methodRefs);
        parseAndSaveArguments(methodSignature);
    }
}

void ClassParser::saveSuperClassName(){
    if(superClassNameIndex > 0) { // May be zero -> class is java.lang.Object
        superClassName = constantPool->getConstantString(superClassNameIndex,
                                                           JavaConst::CONSTANT_Class);
    }
    else {
        // classes which don't have super class have actually java.lang.Object as super class
        superClassName = "java/lang/Object";
    }
}

void ClassParser::saveToAllRefs(std::vector<std::string> &from) {
    for(auto &ref: from){
        if (std::find(allRefs.begin(), allRefs.end(), ref) == allRefs.end()) {
            allRefs.push_back(ref);
        }
    }

}


void ClassParser::parse() {
    readMagicId();
    readVersions();
    readConstantPool();
    readClassInfo();
    readInterfaces();
    readFields();
    readMethods();
    saveSuperClassName();


    allRefs.push_back(superClassName);
    saveToAllRefs(interfacesRefs);
    saveToAllRefs(fieldRefs);
    saveToAllRefs(methodRefs);
}


ClassParser* ClassParser::parse(const unsigned char *class_data, jint class_data_len){
    ByteReader reader(class_data, class_data_len);
    ClassParser* parser = new ClassParser(reader);
    parser->parse();

    return parser;
}

std::string ClassParser::getSuperClassRef() {
    return superClassName;
}

std::vector<std::string> ClassParser::getInterfacesRefs() {
    return interfacesRefs;
}

std::vector<std::string> ClassParser::getFieldRefs() {
    return fieldRefs;
}

std::vector<std::string> ClassParser::getMethodRefs() {
    return methodRefs;
}

std::vector<std::string> ClassParser::getAllRefs() {
    return allRefs;
}












