//
// Created by Jakub Háva on 24/10/2016.
//

#ifndef DISTRACE_AGENT_CORE_CLASSPARSER_H
#define DISTRACE_AGENT_CORE_CLASSPARSER_H


#include "../utils/ByteReader.h"
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
    static ClassParser* parse(const unsigned char *classData, jint classDataLen);

    /**
     * Parse the bytecode and get just the name of the class, skipping the rest of all steps
     */
    static std::string parseJustName(const unsigned char *classData, jint classDataLen);

    /**
     * Get fully qualified class name.
     */
    std::string getClassName();
    /**
     * Get fully qualified super class name. If the class doesn't have any super class, java/lang/Object is returned
     */
    std::string getSuperClassRef();

    /**
     * Get fully qualified names of all interfaces
     */
    std::set<std::string> getInterfacesRefs();

    /**
     * Get fully qualified names of all fields
     */
    std::set<std::string> getFieldRefs();

    /**
     * Get fully qualified names of all method return values and arguments
     */
    std::set<std::string> getMethodRefs();

    /**
     * Get fully qualified names of all references in the bytecode ( does not include references in the method bodies )
     */
    std::set<std::string> getAllRefs();

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
     * Parse type signature and return class name if type was a reference and empty string otherwise
     */
    std::string classNameFromSignature(std::string typeSignature);

    void parseAndSaveArguments(std::string methodSignature);

    /**
     * Save class name into specified vector. If class name is empty or class is in java package then the class is not saved
     */
    void saveRefUniquely(std::string className, std::set<std::string> &where);

    /**
     * Save class name into vector containing all references.
     */
    void saveToAllRefs(std::set<std::string> &from);
    static const std::string NOT_A_REF;
    std::string className;
    std::string superClassName;
    ConstantPool* constantPool;
    ByteReader reader;
    short minorVersion;
    short majorVersion;
    int classNameIndex;
    int superClassNameIndex;
    std::set<std::string> allRefs;
    std::set<std::string> fieldRefs;
    std::set<std::string> methodRefs;
    std::set<std::string> interfacesRefs;
    int numInterfaces;
    int *interfaces;
    int numFields;
    Field *fields;
    int numMethods;
    Method *methods;
};


#endif //DISTRACE_AGENT_CORE_CLASSPARSER_H
