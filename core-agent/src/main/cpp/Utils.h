//
// Created by Jakub Háva on 22/04/16.
//

#ifndef DISTRACE_AGENT_CORE_UTILS_H
#define DISTRACE_AGENT_CORE_UTILS_H

#include <string>

namespace Distrace {
    /**
     * This namespace contains various utilities methods
     */
    namespace Utilities {

        /**
         * Convert std::string to char*
         */
        char *stringToCharPointer(std::string input);
    }
}


#endif //DISTRACE_AGENT_CORE_UTILS_H