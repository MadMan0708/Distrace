//
// Created by Jakub Háva on 22/04/16.
//

#include "Utils.h"
#include <boost/filesystem.hpp>
#include <iostream>
#include <sstream>

namespace fs = boost::filesystem;

namespace Distrace {
    namespace Utils {

        std::vector<std::string> splitString(const std::string& input, const std::string& delimiters, bool compressDelimiters){
            std::vector<std::string> splits;
            size_t prev = 0, pos = 0;
            std::string token;
            while ((pos = input.find_first_of(delimiters, prev)) != std::string::npos) {
                token = input.substr(prev, pos - prev);
                if(compressDelimiters){
                    if(!token.empty()){
                        splits.push_back(token);
                    }
                }else{
                    splits.push_back(token);
                }
                prev = pos + 1;
            }
            token = input.substr(prev, input.length() - prev);
            if(compressDelimiters){
                if(!token.empty()){
                    splits.push_back(token);
                }
            }else{
                splits.push_back(token);
            }
            return splits;
        }

        std::string join(std::vector<std::string> tokens, std::string sep){
            std::stringstream ss;
            for(std::vector<std::string>::size_type i = 0; i < tokens.size(); i++) {
                ss << tokens[i];
                if(i < tokens.size() -1 ) {
                    ss << sep;
                }
            }
            return ss.str();
        }

        bool startsWith(const std::string& input, const std::string& start){
            return input.find(start) == 0;
        }

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
            fs::path tmpDir = boost::filesystem::temp_directory_path();
            int tries = 0;
            bool dirCreated = false;
            while (tries <= maxNumTries && !dirCreated) {
                fs::path newTmpDir = tmpDir / boost::filesystem::unique_path();
                dirCreated = boost::filesystem::create_directories(newTmpDir);
                tries++;
                if (dirCreated) {
                    return newTmpDir.string();
                }
            }
            throw std::runtime_error("Tried creating tmp directory for " + std::to_string(maxNumTries) + " times without a success.");
        }
    }
}

