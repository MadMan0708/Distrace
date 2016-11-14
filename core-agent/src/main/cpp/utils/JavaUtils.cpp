//
// Created by Jakub Háva on 13/05/16.
//

#include <boost/algorithm/string.hpp>
#include "JavaUtils.h"
#include "ByteReader.h"

using namespace Distrace::Logging;

namespace Distrace {
    namespace JavaUtils {

        jstring asJavaString(JNIEnv *jni, std::string str){
            return jni->NewStringUTF(str.c_str());
        }

        jclass loadClass(JNIEnv *jni, jobject loader, std::string className){
            jclass clazz = jni->GetObjectClass(loader);
            jmethodID loadClassMethod = jni->GetMethodID(clazz, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
            return (jclass)jni->CallObjectMethod(loader, loadClassMethod, asJavaString(jni, className));
        }

        void triggerLoadingWithSpecificLoader(JNIEnv *jni, std::string className, jobject loader){
            jclass utils = jni->FindClass("cz/cuni/mff/d3s/distrace/Utils");
            jmethodID getClass = jni->GetStaticMethodID(utils, "triggerLoading","(Ljava/lang/String;Ljava/lang/ClassLoader;)V");
            jstring jClassName = asJavaString(jni, className);
            jni->CallStaticVoidMethod(utils, getClass, jClassName, loader);
        }

        int fromInputStream(JNIEnv *jni, jobject inputStream, const unsigned char **buff){
            auto baosClazz = jni->FindClass("java/io/ByteArrayOutputStream");
            auto baosConstructor = jni->GetMethodID(baosClazz, "<init>","()V");
            auto baosWriteMethod = jni->GetMethodID(baosClazz, "write", "(I)V");
            auto baosToArrayMethod = jni->GetMethodID(baosClazz, "toByteArray", "()[B");

            auto baos = jni->NewObject(baosClazz, baosConstructor);

            auto inputStreamClazz = jni->GetObjectClass(inputStream);
            auto readMethod = jni->GetMethodID(inputStreamClazz, "read", "()I");
            auto reads = jni->CallIntMethod(inputStream, readMethod);

            while (reads != -1) {
                jni->CallVoidMethod(baos, baosWriteMethod, reads);
                reads = jni->CallIntMethod(inputStream, readMethod);
            }

            auto byteArray = (jbyteArray) jni->CallObjectMethod(baos, baosToArrayMethod);
            return asUnsignedCharArray(jni, byteArray, buff);

        }

        int getBytesForClass(JNIEnv *jni, std::string className, jobject loader, const unsigned char **buf){
            auto resourceName = toNameWithSlashes(className) + ".class";
            if( loader == NULL){
                return 0;
            }else{
                auto loaderClazz = jni->GetObjectClass(loader);
                auto resourceAsStreamMethod = jni->GetMethodID(loaderClazz, "getResourceAsStream", "(Ljava/lang/String;)Ljava/io/InputStream;");
                auto inputStream = jni->CallObjectMethod(loader, resourceAsStreamMethod, asJavaString(jni, resourceName));
                if(inputStream == NULL){
                    return 0;
                }else{
                    return fromInputStream(jni, inputStream, buf);
                }
            }
        }

        int asUnsignedCharArray(JNIEnv *jni, jbyteArray input, const unsigned char **output) {
            int len = jni->GetArrayLength (input);
            unsigned char *buffer = new unsigned char[len];
            jni->GetByteArrayRegion (input, 0, len, reinterpret_cast<jbyte*>(buffer));
            *output = buffer;
            return len;
        }

        jbyteArray asJByteArray(JNIEnv *jni, unsigned char *data, int dataLen){
            jbyteArray array = jni->NewByteArray (dataLen);
            jni->SetByteArrayRegion (array, 0, dataLen, reinterpret_cast<jbyte*>(data));
            return array;
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

