//
// Created by Jakub Háva on 28/11/2016.
//

#include <jni.h>
#include "Agent.h"
#include "NativeMethodsHelper.h"
#include "utils/JavaUtils.h"
#include "utils/Utils.h"

using namespace Distrace;
using namespace Distrace::Logging;

jstring NativeMethodsHelper::getSaverType(JNIEnv *jni, jobject thiz){
   return JavaUtils::asJavaString(jni, Agent::getArgs()->getArgValue(AgentArgs::ARG_SAVER_TYPE));
}

jboolean NativeMethodsHelper::isDebugging(JNIEnv *jni, jobject thiz){
    return (jboolean) (Agent::getArgs()->getArgValue(AgentArgs::ARG_LOG_LEVEL) == "debug");
}

std::map<std::string, std::vector<JNINativeMethod>> NativeMethodsHelper::nativesPerClass = {
        {
                std::make_pair<std::string, std::vector<JNINativeMethod>>(
                        "cz.cuni.mff.d3s.distrace.tracing.Span",
                        {
                                toNative("getSaverType", "()Ljava/lang/String;", (void *) getSaverType)
                        }
                ),
                std::make_pair<std::string, std::vector<JNINativeMethod>>(
                        "cz.cuni.mff.d3s.distrace.storage.SpanSaver",
                        {
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








