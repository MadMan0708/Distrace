//
// Created by Jakub Háva on 13/05/16.
//

#ifndef DISTRACE_AGENT_CORE_JAVAUTILS_H
#define DISTRACE_AGENT_CORE_JAVAUTILS_H


#include <jni.h>
#include <string>
namespace DistraceAgent{
    class JavaUtils {
    public:
        /**
         * Gets fully qualified class name of class
         */
        static std::string getClassName(JNIEnv* env, jclass klazz);

        /**
        * Gets fully qualified class name of object's class
        */
        static std::string getObjectClassName(JNIEnv *env, jobject object);

        /**
         * Gets fully qualified class name of provided classloader. Loader set to NULL represents bootstrap classloader.
         */
        static std::string getClassLoaderName(JNIEnv* env, jobject loader);
    };
}

#endif //DISTRACE_AGENT_CORE_JAVAUTILS_H
