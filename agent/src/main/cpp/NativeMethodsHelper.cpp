//
// Created by Jakub Háva on 28/11/2016.
//

#include <jni.h>
#include "Agent.h"
#include "NativeMethodsHelper.h"
#include "utils/JavaUtils.h"
#include "utils/Utils.h"
#include "sole/sole.hpp"

using namespace Distrace;
using namespace Distrace::Logging;

jstring NativeMethodsHelper::getSpanExporterType(JNIEnv *jni, jobject thiz){
   return JavaUtils::asJavaString(jni, Agent::getArgs()->getArgValue(AgentArgs::ARG_SPAN_EXPORTER_TYPE));
}

jboolean NativeMethodsHelper::isDebugging(JNIEnv *jni, jobject thiz){
    return (jboolean) (Agent::getArgs()->getArgValue(AgentArgs::ARG_LOG_LEVEL) == "debug");
}

jstring NativeMethodsHelper::getTypeOneUUID(JNIEnv *jni, jobject thiz){
    return JavaUtils::asJavaString(jni, sole::uuid0().str());
}


std::map<std::string, std::vector<JNINativeMethod>> NativeMethodsHelper::nativesPerClass = {
    {
        std::make_pair<std::string, std::vector<JNINativeMethod>>(
            "cz.cuni.mff.d3s.distrace.tracing.Span",
            {
                    toNative("getSpanExporterType", "()Ljava/lang/String;", (void *) getSpanExporterType)
            }
        ),
        std::make_pair<std::string, std::vector<JNINativeMethod>>(
            "cz.cuni.mff.d3s.distrace.utils.NativeAgentUtils",
            {
                    toNative("getTypeOneUUID", "()Ljava/lang/String;", (void *) getTypeOneUUID),
                    toNative("isDebugging", "()Z", (void *) isDebugging)
            }
        )
    }
};

void NativeMethodsHelper::loadNativesFor(JNIEnv* jni, jclass klazz, std::string className){
    for (auto& kv : nativesPerClass) {
        if(kv.first == className){
            log(LOGGER_AGENT_CALLBACKS)->info("Registering native methods for {}", className);
            jni->RegisterNatives(klazz, &kv.second[0], (jint) kv.second.size());
        }
    }
}

JNINativeMethod NativeMethodsHelper::toNative(std::string name, std::string signature, void *method) {
    return  JNINativeMethod{Utils::stringToCharPointer(name), Utils::stringToCharPointer(signature), method};
}








