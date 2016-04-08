//
// Created by Jakub Háva on 08/04/16.
//

#include "AgentUtils.h"

namespace DistraceAgent {

    static int AgentUtils::check_jvmti_error(jvmtiEnv *env, jvmtiError error_number, const char *error_description) {
        if (error_number != JVMTI_ERROR_NONE) {
            char *error_name = NULL;
            env->GetErrorName(error_number, &error_name);

            printf("ERROR: JVMTI: %d(%s): %s\n", error_number,
                   (error_name == NULL ? "Unknown" : error_name),
                   (error_description == NULL ? "" : error_description));
            return JNI_ERR;
        }
        return JNI_OK;
    }
}
