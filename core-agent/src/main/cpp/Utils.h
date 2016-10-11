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
         * Convert std::string to char*
         */
        char *stringToCharPointer(std::string input);

        /**
         * Create directories specified by directories path. Return true in case of success and false otherwise
         */
        bool create_directories(std::string dir_path);


        std::string unique_tmp_dir_path();
    }
}


#endif //DISTRACE_AGENT_CORE_UTILS_H