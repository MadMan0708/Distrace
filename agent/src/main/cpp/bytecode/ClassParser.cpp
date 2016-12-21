//
// Created by Jakub Háva on 24/10/2016.
//

#include "ClassParser.h"
#include "JavaConst.h"
#include "../utils/JavaUtils.h"
#include "../utils/Utils.h"


using namespace Logging;

const std::string ClassParser::NOT_A_REF = "";

ClassParser::ClassParser(ByteReader &reader) : reader(reader) {
}

void ClassParser::readMagicId(){
    int magicId = reader.readInt();
    int JVM_CLASSFILE_MAGIC = 0xCAFEBABE;
    if(JVM_CLASSFILE_MAGIC != magicId){
        throw std::runtime_error("Magic id is not correct");
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
        throw std::runtime_error("Class can't be both final and abstract");
    }
    // read and save class name
    classNameIndex = reader.readShort();
    className = constantPool->getConstantString(classNameIndex, JavaConst::CONSTANT_Class);

    // read and save super class name
    superClassNameIndex = reader.readShort();
    if(superClassNameIndex > 0) { // May be zero -> class is java.lang.Object
        superClassName = constantPool->getConstantString(superClassNameIndex, JavaConst::CONSTANT_Class);
    }
    else {
        // classes which don't have super class have actually java.lang.Object as super class
        superClassName = "java/lang/Object";
    }
}

void ClassParser::readInterfaces(){
    numInterfaces = reader.readShort();
    interfaces = new int[numInterfaces];
    // there can't be duplicate interfaces in a class, se we can store them without looking if the interface is
    // already stored
    for (int i = 0; i < numInterfaces; i++) {
        interfaces[i] = reader.readShort();
        std::string interfaceName = constantPool->getConstantString(interfaces[i], JavaConst::CONSTANT_Class);
        interfacesRefs.insert(interfaceName);
    }
}


std::string ClassParser::classNameFromSignature(std::string typeSignature){
    if(Utils::startsWith(typeSignature, "L")){
        std::string trimmed = typeSignature.substr(1, typeSignature.length() - 2); // remove leading L and ; and the end
        return trimmed;
    }else{
        return "";
    }
}

void ClassParser::saveRefUniquely(std::string ref, std::set<std::string> &where){
    // we are only interested in references
    if(ref != NOT_A_REF){
        where.insert(ref);
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

void ClassParser::parseAndSaveArguments(std::string arguments){
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

        std::string signature = methods[i].getSignature();
        // splits[0] is empty string  - we cut off first parentheses
        // splits[1] are arguments type signatures
        // splits[2] is return type signature
        std::vector<std::string> splits = Utils::splitString(signature, "()");

        parseAndSaveArguments(splits[1]);
        std::string ref = classNameFromSignature(splits[2]);
        saveRefUniquely(ref, methodRefs);
    }
}

void ClassParser::saveToAllRefs(std::set<std::string> &from) {
    for(auto &ref: from){
        allRefs.insert(ref);
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

    allRefs.insert(superClassName);
    saveToAllRefs(interfacesRefs);
    saveToAllRefs(fieldRefs);
    saveToAllRefs(methodRefs);
}


ClassParser* ClassParser::parse(const unsigned char *classData, jint classDataLen){
    ByteReader reader(classData, classDataLen);
    ClassParser* parser = new ClassParser(reader);
    parser->parse();

    return parser;
}

std::string ClassParser::parseJustName(const unsigned char *classData, jint classDataLen) {
    ByteReader reader(classData, classDataLen);
    ClassParser* parser = new ClassParser(reader);
    parser->readMagicId();
    parser->readVersions();
    parser->readConstantPool();
    parser->readClassInfo();
    std::string className = parser->getClassName();
    delete parser;
    return className;
}

std::string ClassParser::getClassName(){
    return className;
}

std::string ClassParser::getSuperClassRef() {
    return superClassName;
}

std::set<std::string> ClassParser::getInterfacesRefs() {
    return interfacesRefs;
}

std::set<std::string> ClassParser::getFieldRefs() {
    return fieldRefs;
}

std::set<std::string> ClassParser::getMethodRefs() {
    return methodRefs;
}

std::set<std::string> ClassParser::getAllRefs() {
    return allRefs;
}













