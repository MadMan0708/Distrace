//
// Created by Jakub Háva on 22/04/16.
//

#include "Utils.h"
#include <boost/filesystem.hpp>

namespace Distrace {
    namespace Utils {
        char *stringToCharPointer(std::string input) {
            char *str_copy = new char[input.size() + 1];
            strcpy(str_copy, input.c_str());
            return str_copy;
        }

        bool createDirectories(std::string dirPath) {
            boost::filesystem::path dir(dirPath);
            return boost::filesystem::create_directories(dir);
        }

        std::string createUniqueTempDir() {
            int maxNumTries = 100;
            std::string prefix = boost::filesystem::temp_directory_path().string();
            int tries = 0;
            bool dirCreated = false;
            while (tries <= maxNumTries && !dirCreated) {
                std::string path = prefix + boost::filesystem::unique_path().string()
                                   + boost::filesystem::path::preferred_separator;
                dirCreated = boost::filesystem::create_directories(path);
                tries++;
                if (dirCreated) {
                    return path;
                }
            }
            throw std::runtime_error("Tried creating tmp directory for " + std::to_string(maxNumTries) + " times without a success.");
        }
    }
}

