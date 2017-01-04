//
// Created by Jakub Háva on 22/04/16.
//

#ifndef DISTRACE_AGENT_CORE_UTILS_H
#define DISTRACE_AGENT_CORE_UTILS_H

#include <string>
#include <vector>

namespace Distrace {
    /**
     * This namespace contains various utilities methods
     */
    namespace Utils {

        const bool token_compress_on = true;
        const bool token_compress_off = false;
        /**
         * Split string where delimiters is a string containing delimiters. Delimiter is always one char.
         */
        std::vector<std::string> splitString(const std::string& input, const std::string& delimiters, bool compressDelimiters = token_compress_off);

        /**
         * join vector elements by separator
         */
        std::string join(std::vector<std::string> tokens, std::string sep);

        /**
         * Check if string starts with specified string
         */
        bool startsWith(const std::string& input, const std::string& start);

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