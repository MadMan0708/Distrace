//
// Created by Jakub Háva on 22/04/16.
//

#include "Utils.h"


namespace Distrace {
    namespace Utilities {
        char *stringToCharPointer(std::string input) {
            char *str_copy = new char[input.size() + 1];
            strcpy(str_copy, input.c_str());
            return str_copy;
        }
    }
}

