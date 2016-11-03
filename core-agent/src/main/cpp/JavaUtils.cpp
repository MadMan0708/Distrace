//
// Created by Jakub Háva on 13/05/16.
//

#include <boost/algorithm/string.hpp>
#include "JavaUtils.h"
#include "ByteReader.h"

using namespace Logging;
namespace Distrace {
    namespace JavaUtils {

        jstring asJavaString(JNIEnv *env, std::string str){
            return env->NewStringUTF(str.c_str());
        }

        int getBytesForClass(JNIEnv *env, std::string className, jobject loader, unsigned char **buf){
            jclass utils = env->FindClass("cz/cuni/mff/d3s/distrace/Utils");
            jmethodID  getBytesMethod = env->GetStaticMethodID(utils,"getBytesFromClassFile","(Ljava/lang/String;Ljava/lang/ClassLoader;)[B");
            jstring jClassName = asJavaString(env, className);
            jbyteArray  arr = (jbyteArray) env->CallStaticObjectMethod(utils, getBytesMethod, jClassName, loader);
            int len = env->GetArrayLength (arr);

            unsigned char* buffer = new unsigned char[len];
            env->GetByteArrayRegion (arr, 0, len, reinterpret_cast<jbyte*>(buffer));
            *buf = buffer;
            return len;
        }

        unsigned char* as_unsigned_char_array(JNIEnv *env, jbyteArray array) {
            int len = env->GetArrayLength (array);
            unsigned char* buf = new unsigned char[len];
            env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
            return buf;
        }

        std::string toNameWithDots(std::string name){
            std::string class_name(name);
            std::replace(class_name.begin(), class_name.end(), '/', '.');
            return class_name;
        }

        /**
        *  The list of class loaders for which we don't want to instrument classes loaded by these class loaders
        */
        std::vector<std::string> ignoredLoaders =  {
                // bootstrap classloader, it loads system classes which we usually don't want to instrument
                "@Bootstrap",
                "sun.reflect.DelegatingClassLoader"
                // classloader created because of mechanism called "inflatation", it is created synthetically and loads synthetical classes
                // and we do not want to create type Descriptions for these internal java classes
                // see http://stackoverflow.com/questions/6505274/what-for-sun-jvm-creates-instances-of-sun-reflect-delegatingclassloader-at-runti
        };

        bool isIgnoredClassLoader(std::string cl){
            return std::find(ignoredLoaders.begin(), ignoredLoaders.end(), cl) != ignoredLoaders.end();
        }

        std::string getClassName(JNIEnv *env, jclass klazz) {
            // Get the class object's class descriptor (jclass inherits from jobject)
            jclass clsClazz = env->GetObjectClass(klazz);
            // Find the getName() method in the class object
            jmethodID methodId = env->GetMethodID(clsClazz, "getName", "()Ljava/lang/String;");

            jstring className = (jstring) env->CallObjectMethod(klazz, methodId);

            // Now get the c string from the java jstring object
            const char *str = env->GetStringUTFChars(className, NULL);
            std::string classNameCStr(str);
            env->ReleaseStringUTFChars(className, str);
            return classNameCStr;
        }

        jobject getClassLoaderForClass(JNIEnv *env, jclass klazz){
            // Get the class object's class descriptor (jclass inherits from jobject)
            jclass clsClazz = env->GetObjectClass(klazz);
            // Find the getClassLoader() method in the class object
            jmethodID methodId = env->GetMethodID(clsClazz, "getClassLoader", "()Ljava/lang/ClassLoader;");
            return (jobject) env->CallObjectMethod(klazz, methodId);
        }

        std::string getObjectClassName(JNIEnv *env, jobject instance) {
            // Get the class of the object
            jclass cls = env->GetObjectClass(instance);
            return getClassName(env, cls);
        }

        std::string getClassLoaderName(JNIEnv *env, jobject loader) {
            if (loader == NULL) {
                return "@Bootstrap";
                // we put @ sign before it to prevent collision with class
                // which could be potentially named Bootstrap in an empty package. Packages can't start with @
                // so this will keep us safe
            } else {
                return getObjectClassName(env, loader);
            }
        }

    }
}

