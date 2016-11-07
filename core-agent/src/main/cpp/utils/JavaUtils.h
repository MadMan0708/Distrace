//
// Created by Jakub Háva on 13/05/16.
//

#ifndef DISTRACE_AGENT_CORE_JAVAUTILS_H
#define DISTRACE_AGENT_CORE_JAVAUTILS_H


#include <jni.h>
#include <string>
#include "Logging.h"

namespace Distrace{
    /**
     * This namespace contains various utilities methods to work with JVM and Java objects
     */
    namespace JavaUtils {

        /**
         * Converts std::string to jstring
         */
        jstring asJavaString(JNIEnv *jni, std::string str);

        /**
         * Trigger class loading of specified class using the provided class loader
         */
        void triggerLoadingWithSpecificLoader(JNIEnv *jni, std::string className, jobject loader);

        /**
         * Obtains byte array containing bytecode for class className
         */
        int getBytesForClass(JNIEnv *jni, std::string className, jobject loader, const unsigned char **buf);

        /**
         * Converts jbytearray to char*
         */
        int asUnsignedCharArray(JNIEnv *jni, jbyteArray input, const unsigned char **output);

        /**
         * Converts fully qualified name separated by slashed to the same name separated by dots
         */
        std::string toNameWithDots(std::string className);

        /**
         * Converts fully qualified name separated by dots to the same name separated by slashes
         */
        std::string toNameWithSlashes(std::string className);

        /**
         * Get fully qualified class name of class
         */
        std::string getClassName(JNIEnv *jni, jclass clazz);

        /**
         * Get fully qualified class name of object's class
         */
        std::string getObjectClassName(JNIEnv *jni, jobject object);

        /**
         * Get fully qualified class name of provided classloader. Loader is set to NULL represents bootstrap classloader.
         */
        std::string getClassLoaderName(JNIEnv *jni, jobject loader);

        /*
         * Get classloader which was used to load the class in the argument
         */
        jobject getClassLoaderForClass(JNIEnv *jni, jclass clazz);

    }
}

#endif //DISTRACE_AGENT_CORE_JAVAUTILS_H
