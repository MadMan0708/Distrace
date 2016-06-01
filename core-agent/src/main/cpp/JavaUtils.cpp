//
// Created by Jakub Háva on 13/05/16.
//

#include "JavaUtils.h"

namespace Distrace {
    namespace JavaUtils {

        std::string getClassName(JNIEnv *env, jclass klazz) {
            // Get the class object's class descriptor (jclass inherits from jobject)
            jclass clsClazz = env->GetObjectClass(klazz);
            // Find the getName() method in the class object
            jmethodID methodId = env->GetMethodID(clsClazz, "getName", "()Ljava/lang/String;");
            jstring className = (jstring) env->CallObjectMethod(klazz, methodId);
            // Now get the c string from the java jstring object
            const char *str = env->GetStringUTFChars(className, NULL);
            return str;
        }

        std::string getObjectClassName(JNIEnv *env, jobject instance) {
            // Get the class of the object
            jclass cls = env->GetObjectClass(instance);
            return getClassName(env, cls);
        }

        std::string getClassLoaderName(JNIEnv *env, jobject loader) {
            if (loader == NULL) {
                return "Bootstrap";
            } else {
                return getObjectClassName(env, loader);
            }
        }

    }
}

