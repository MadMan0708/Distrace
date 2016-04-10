//
// Created by Jakub Háva on 08/04/16.
//

#ifndef DISTRACE_AGENT_CORE_AGENTA_H
#define DISTRACE_AGENT_CORE_AGENTA_H

namespace DistraceAgent {
    typedef struct {
        /* JVMTI Environment */
        jvmtiEnv *jvmti = NULL;
        JNIEnv *jni = NULL;
        JavaVM *jvm = NULL;
        jboolean vm_started = (jboolean) false;
        jboolean vm_dead = (jboolean) false;
    } GlobalAgentData;

    class Agent {
    public:
        static GlobalAgentData* globalData;
        static void init_global_data();
    };
}

#endif //DISTRACE_AGENT_CORE_AGENTA_H