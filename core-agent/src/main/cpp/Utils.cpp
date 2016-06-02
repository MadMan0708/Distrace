//
// Created by Jakub Háva on 22/04/16.
//

#include "Utils.h"
#include <boost/filesystem.hpp>

namespace Distrace {
    namespace Utilities {
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
    }
}

