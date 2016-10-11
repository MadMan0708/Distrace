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

        bool create_directories(std::string dir_path){
            boost::filesystem::path dir(dir_path);
            if(boost::filesystem::create_directories(dir)){
                return true;
            }else{
                return false;
            }
        }

        std::string unique_tmp_dir_path(){
            int max_num_tries = 100;
            std::string prefix = boost::filesystem::temp_directory_path().string();
            int tries = 0;
            bool created = false;
            while(tries <= max_num_tries && !created){
                std::string path = prefix + boost::filesystem::unique_path().string()
                                    + boost::filesystem::path::preferred_separator;
                created = boost::filesystem::create_directories(path);
                tries++;
                if(created){
                    return path;
                }
            }
            throw "Tried creating tmp directory for " + std::to_string(max_num_tries) + " times without a success";
        }
    }
}

