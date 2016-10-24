//
// Created by Jakub Háva on 24/10/2016.
//

#include <boost/algorithm/string.hpp>
#include "ClassParser.h"
#include "JavaConst.h"


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
    /* Interfaces are implicitely abstract, the flag should be set
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
    for (int i = 0; i < numInterfaces; i++) {
        interfaces[i] = reader.readShort();
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

void ClassParser::saveUniqueClass(std::string className){
    // we are only interested in references which does not belong to java package

    if(!(className.empty() || boost::starts_with(className, "java"))){
        if (std::find(uniqueTypes.begin(), uniqueTypes.end(), className) == uniqueTypes.end()) {
            // put the ref to the list of unique refs to be loaded
            uniqueTypes.push_back(className);
        }
    }
}


void ClassParser::readFields(){
    numFields = reader.readShort();

    fields = new Field[numFields];
    for (int i = 0; i < numFields; i++) {
        fields[i] = *(new Field(reader, *constantPool));
        std::string signature = fields[i].getSignature();
        std::string className = classNameFromSignature(signature);
        saveUniqueClass(className);
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

        std::string className = classNameFromSignature(argSignature);
        saveUniqueClass(className);
        arguments = trimmedStart.substr(end);

    }
}

void ClassParser::readMethods(){
    numMethods = reader.readShort();

    methods = new Method[numMethods];
    for (int i = 0; i < numMethods; i++) {
        methods[i] = (*new Method(reader, *constantPool));
        std::string methodSignature = methods[i].getSignature();

        std::string className = returnValueFromSignature(methodSignature);
        saveUniqueClass(className);
        parseAndSaveArguments(methodSignature);
    }
}

std::string ClassParser::saveSuperClassName(){
    std::string superclass_name;
    if(superClassNameIndex > 0) { // May be zero -> class is java.lang.Object
        superclass_name = constantPool->getConstantString(superClassNameIndex,
                                                           JavaConst::CONSTANT_Class);
    }
    else {
        superclass_name = "java/lang/Object";
    }
    saveUniqueClass(superclass_name);
    return superclass_name;
}

void ClassParser::parse(std::string className) {
    readMagicId();
    readVersions();
    readConstantPool();
    readClassInfo();
    readInterfaces();
    readFields();
    readMethods();
    saveSuperClassName();
    // TODO: Save interfaces

    // log obtained classes
    std::vector<std::string>::iterator it;
    for(it=uniqueTypes.begin() ; it <uniqueTypes.end(); it++) {
        log(LOGGER_BYTECODE)->info() << "Parsed class: " << *it;
    }

    // remove references to class currently being loaded ( it could be obtained in case the class is using itself as
    // a field/ method argument/ return type
    uniqueTypes.erase(std::remove(uniqueTypes.begin(), uniqueTypes.end(), className), uniqueTypes.end());
}


std::vector<std::string> ClassParser::parse(std::string className, const unsigned char *class_data, jint class_data_len){
    ByteReader reader(class_data, class_data_len);
    ClassParser* parser = new ClassParser(reader);
    parser->parse(className);
    return parser->uniqueTypes;
}


