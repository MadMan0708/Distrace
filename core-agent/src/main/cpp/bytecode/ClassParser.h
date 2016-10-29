//
// Created by Jakub Háva on 24/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CLASSPARSER_H
#define DISTRACE_AGENT_CORE_CLASSPARSER_H


#include "../ByteReader.h"
#include "ConstantPool.h"
#include "Field.h"
#include "Method.h"

using namespace Distrace;


class ClassParser {

public:
    ClassParser(ByteReader &reader);
    void parse();
    static std::vector<std::string> parse(const unsigned char *class_data, jint class_data_len);
    void readMagicId();
    void readVersions();
    void readConstantPool();
    void readClassInfo();
    void readInterfaces();
    void readFields();
    void readMethods();
    /**
     * Returns class name if typeSignature represents reference and empty string otherwise
     */
    std::string classNameFromSignature(std::string typeSignature);
    std::string returnValueFromSignature(std::string methodSignature);
    void parseAndSaveArguments(std::string methodSignature);
    /**
     * Save class name. If class name is empty or class is in java package then the class is not saved
     */
    void saveUniqueClass(std::string className);
    void saveSuperClassName();

private:
    ConstantPool* constantPool;
    ByteReader reader;
    short minorVersion;
    short majorVersion;
    int classNameIndex;
    int superClassNameIndex;
    std::vector<std::string> uniqueTypes;
    int numInterfaces;
    int *interfaces;
    int numFields;
    Field *fields;
    int numMethods;
    Method *methods;

};


#endif //DISTRACE_AGENT_CORE_CLASSPARSER_H
