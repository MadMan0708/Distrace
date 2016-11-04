//
// Created by Jakub Háva on 13/05/16.
//

#include <boost/algorithm/string.hpp>
#include "JavaUtils.h"
#include "ByteReader.h"

using namespace Distrace::Logging;

namespace Distrace {
    namespace JavaUtils {

        jstring asJavaString(JNIEnv *env, std::string str){
            return env->NewStringUTF(str.c_str());
        }

        int getBytesForClass(JNIEnv *jni, std::string className, jobject loader, const unsigned char **buf){
            jclass utils = jni->FindClass("cz/cuni/mff/d3s/distrace/Utils");
            jmethodID  getBytesMethod = jni->GetStaticMethodID(utils,"getBytesFromClassFile","(Ljava/lang/String;Ljava/lang/ClassLoader;)[B");
            jstring jClassName = asJavaString(jni, className);
            jbyteArray array = (jbyteArray) jni->CallStaticObjectMethod(utils, getBytesMethod, jClassName, loader);
            return asUnsignedCharArray(jni, array, buf);
        }

        int asUnsignedCharArray(JNIEnv *env, jbyteArray input, const unsigned char **output) {
            int len = env->GetArrayLength (input);
            unsigned char *buffer = new unsigned char[len];
            env->GetByteArrayRegion (input, 0, len, reinterpret_cast<jbyte*>(buffer));
            *output = buffer;
            return len;
        }

        std::string toNameWithDots(std::string className){
            std::string newClassName(className);
            std::replace(newClassName.begin(), newClassName.end(), '/', '.');
            return newClassName;
        }

        std::string toNameWithSlashes(std::string className){
            std::string newClassName(className);
            std::replace(newClassName.begin(), newClassName.end(), '.', '/');
            return newClassName;
        }

        std::string getClassName(JNIEnv *jni, jclass clazz) {
            // Get the class object's class descriptor (jclass inherits from jobject)
            jclass clsClazz = jni->GetObjectClass(clazz);
            // Find the getName() method in the class object
            jmethodID methodId = jni->GetMethodID(clsClazz, "getName", "()Ljava/lang/String;");

            jstring className = (jstring) jni->CallObjectMethod(clazz, methodId);

            // Now get the c string from the java jstring object
            const char *str = jni->GetStringUTFChars(className, NULL);
            std::string classNameCStr(str);
            jni->ReleaseStringUTFChars(className, str);
            return classNameCStr;
        }

        std::string getObjectClassName(JNIEnv *jni, jobject instance) {
            // Get the class of the object
            jclass cls = jni->GetObjectClass(instance);
            return getClassName(jni, cls);
        }

        std::string getClassLoaderName(JNIEnv *jni, jobject loader) {
            if (loader == NULL) {
                return "@Bootstrap";
                // we put @ sign before it to prevent collision with class
                // which could be potentially named Bootstrap in an empty package. Packages can't start with @
                // so this will keep us safe
            } else {
                return getObjectClassName(jni, loader);
            }
        }

        jobject getClassLoaderForClass(JNIEnv *jni, jclass clazz){
            // Get the class object's class descriptor (jclass inherits from jobject)
            jclass clsClazz = jni->GetObjectClass(clazz);
            // Find the getClassLoader() method in the class object
            jmethodID methodId = jni->GetMethodID(clsClazz, "getClassLoader", "()Ljava/lang/ClassLoader;");
            return (jobject) jni->CallObjectMethod(clazz, methodId);
        }

    }
}

