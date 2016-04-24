//
// Created by Jakub Háva on 22/04/16.
//

#ifndef DISTRACE_AGENT_CORE_UTILS_H
#define DISTRACE_AGENT_CORE_UTILS_H

#include <string>

namespace DistraceAgent {

    class Utils {
    public:
        static char* stringToCharPointer(std::string input);
    };
}


#endif //DISTRACE_AGENT_CORE_UTILS_H
