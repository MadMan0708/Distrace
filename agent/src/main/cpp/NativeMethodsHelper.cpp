//
// Created by Jakub Háva on 28/11/2016.
//

#include <jni.h>
#include "Agent.h"
#include "NativeMethodsHelper.h"
#include "utils/JavaUtils.h"

using namespace Distrace;
using namespace Distrace::Logging;

jstring NativeMethodsHelper::getSaverType(JNIEnv *jni, jobject thiz){
   return JavaUtils::asJavaString(jni, Agent::getArgs()->getArgValue(AgentArgs::ARG_SAVER_TYPE));
}

std::map<std::string, std::vector<JNINativeMethod>> NativeMethodsHelper::nativesPerClass = {
        {
                std::make_pair<std::string, std::vector<JNINativeMethod>>(
                        "cz.cuni.mff.d3s.distrace.api.Span",
                        {
                                {"getSaverType", "()Ljava/lang/String;", (void *) getSaverType}
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






