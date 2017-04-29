//
// Created by Jakub Háva on 28/11/2016.
//

#ifndef DISTRACE_AGENT_NATIVEMETHODS_H
#define DISTRACE_AGENT_NATIVEMETHODS_H

#include <jni.h>
#include <string>
#include <map>
#include <vector>

namespace Distrace {

    /**
     * This namespace contains native methods we are registering
     */
    class NativeMethodsHelper{
    public:

        /**
         * Create JNINative Method
         */
        static JNINativeMethod toNative(std::string name, std::string signature, void* method);

        /**
         * Get span exporter type from the arguments
         */
        static jstring getSpanExporterType(JNIEnv *jni, jobject thiz);

        /**
         * Returns true if the agent is running in debugging mode
         */
        static jboolean isDebugging(JNIEnv *jni, jobject thiz);

        /**
         * Get type one uuid ( clock_seq, mac address, timestamp and version )
         */
        static jstring getTypeOneUUID(JNIEnv *jni, jobject thiz);

        /**
         * List of all native defined native methods
         */
        static std::map<std::string, std::vector<JNINativeMethod>> nativesPerClass;

        /**
         * Loads native methods for provided class
         */
        static void loadNativesFor(JNIEnv* jni, jclass klazz, std::string className);
    };

}


#endif //DISTRACE_AGENT_NATIVEMETHODS_H
