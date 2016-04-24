//
// Created by Jakub Háva on 22/04/16.
//

#include "Utils.h"


using namespace DistraceAgent;

char* Utils::stringToCharPointer(std::string input) {
    char *str_copy = new char[input.size()+1] ;
    strcpy(str_copy, input.c_str());
    return str_copy;
}