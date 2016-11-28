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
    namespace Utils {

        /**
         * Converts std::string to char*
         */
        char *stringToCharPointer(std::string input);

        /**
         * Create directories specified by directories path. Returns true in case of success and false otherwise.
         */
        bool createDirectories(std::string dirPath);

        /**
         * Creates unique temp dir and returns path to it.
         */
        std::string createUniqueTempDir();
    }
}


#endif //DISTRACE_AGENT_CORE_UTILS_H