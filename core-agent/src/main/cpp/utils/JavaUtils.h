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
         * Get hashcode of provided Java object
         */
        jint getHashCode(JNIEnv *jni, jobject object);

        /**
         * Converts std::string to jstring
         */
        jstring asJavaString(JNIEnv *jni, std::string str);

        /**
         * Load class using the specified class loader
         */
        jclass loadClass(JNIEnv *jni, jobject loader, std::string className);

        /**
         * Trigger class loading of specified class using the provided class loader ( via Java code)
         */
        void triggerLoadingWithSpecificLoader(JNIEnv *jni, std::string className, jobject loader);

        /**
         * Check if the class is loaded by specified class loader and returns true if it is, otherwise return false
         */
        bool isClassLoaded(JNIEnv *jni, jobject loader, std::string className);

        /**
         * Get array of bytes from Java input stream
         */
        int fromInputStream(JNIEnv *jni, jobject inputStream, const unsigned char **buf);

        /**
         * Obtains byte array containing bytecode for class className
         */
        int getBytesForClass(JNIEnv *jni, std::string className, jobject loader, const unsigned char **buf);

        /**
         * Converts jbytearray to char*
         */
        int asUnsignedCharArray(JNIEnv *jni, jbyteArray input, const unsigned char **output);

        /**
         * Converts char* to jbytearray
         */
        jbyteArray asJByteArray(JNIEnv *jni, unsigned char *data, int dataLen);

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
