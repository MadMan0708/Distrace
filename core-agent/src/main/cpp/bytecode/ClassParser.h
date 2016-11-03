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

/**
 * Class used to parse raw bytecode. The result of parse are fully qualified names of all references
 */
class ClassParser {

public:

    /**
     * Parse the bytecode and find all references to other classes
     * Super class reference, interfaces references, field references and method return and arguments are processed
     */
    static ClassParser* parse(const unsigned char *class_data, jint class_data_len);

    /**
     * Get fully qualified super class name. If the class doesn't have any super class, java/lang/Object is returned
     */
    std::string getSuperClassRef();

    /**
     * Get fully qualified names of all interfaces
     */
    std::vector<std::string> getInterfacesRefs();

    /**
     * Get fully qualified names of all fields
     */
    std::vector<std::string> getFieldRefs();

    /**
     * Get fully qualified names of all method return values and arguments
     */
    std::vector<std::string> getMethodRefs();

    /**
     * Get fully qualified names of all references in the bytecode ( does not include references in the method bodies )
     */
    std::vector<std::string> getAllRefs();
private:
    ClassParser(ByteReader &reader);
    void parse();
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
    void saveRefUniquely(std::string className, std::vector<std::string> &where);
    void saveSuperClassName();
    void saveToAllRefs(std::vector<std::string> &from);
    std::string superClassName;
    ConstantPool* constantPool;
    ByteReader reader;
    short minorVersion;
    short majorVersion;
    int classNameIndex;
    int superClassNameIndex;
    std::vector<std::string> allRefs;
    std::vector<std::string> fieldRefs;
    std::vector<std::string> methodRefs;
    std::vector<std::string> interfacesRefs;
    int numInterfaces;
    int *interfaces;
    int numFields;
    Field *fields;
    int numMethods;
    Method *methods;

};


#endif //DISTRACE_AGENT_CORE_CLASSPARSER_H
