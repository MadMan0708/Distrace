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

        bool isAlreadyLoaded(JNIEnv *env, const char *name);

        std::vector<std::string> getRefToDep(JNIEnv *env, const char *name, const unsigned char *class_data, jint class_data_len);

        std::string toNameWithDots(std::string class_name);
        /**
         * Converts jbytearray to char*
         */
        unsigned char* as_unsigned_char_array(JNIEnv *env, jbyteArray array);

        /**
         *  The list of class loaders for which we don't want to instrument classes loaded by these class loaders
         */
        extern std::vector<std::string> ignoredLoaders;

        void loadClass(JNIEnv *env, jobject loader, const char *className);
        /**
         * Check whether the classloader on the input is ignored classloader
         */
        bool isIgnoredClassLoader(std::string cl);
        /**
         * Get fully qualified class name of class
         */
        std::string getClassName(JNIEnv* env, jclass klazz);

        /*
         * Get classloader which was used to load the class in the argument
         */
        jobject getClassLoaderForClass(JNIEnv* env, jclass klazz);

        /**
         * Get fully qualified class name of object's class
         */
        std::string getObjectClassName(JNIEnv *env, jobject object);

        /**
         * Get fully qualified class name of provided classloader. Loader set to NULL represents bootstrap classloader.
         */
        std::string getClassLoaderName(JNIEnv* env, jobject loader);
    }
}

#endif //DISTRACE_AGENT_CORE_JAVAUTILS_H
