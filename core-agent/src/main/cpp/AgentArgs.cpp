//
// Created by Jakub Háva on 01/06/16.
//

#include <string>
#include <boost/algorithm/string.hpp>
#include <jni.h>
#include "AgentArgs.h"
#include "Logging.h"

using namespace Distrace;
using namespace Distrace::Logging;

// instantiate argument names
const std::string AgentArgs::ARG_INSTRUMENTOR_JAR = "instrumentorJar";
const std::string AgentArgs::ARG_SOCKET_ADDRESS = "sock_address";

const std::string AgentArgs::ARG_LOG_LEVEL = "log_level";
const std::string AgentArgs::ARG_LOG_DIR = "log_dir";

std::map<std::string, std::string> AgentArgs::getArgsMap() {
    return args;
};

std::string AgentArgs::get_arg_value(std::string arg_name) {
    if(is_arg_set(arg_name)){
        return args.find(arg_name)->second;
    }else{
        log(LOGGER_AGENT)->error() << "Argument \"" << arg_name << "\" is not set. Before getting the value check if it's set using the is_arg_set method";
        assert(false);
    }
}

bool AgentArgs::is_arg_set(std::string arg_name) {
     return args.count(arg_name) != 0;
}

int AgentArgs::validate_log_level(std::string &err_msg){
    if (is_arg_set(AgentArgs::ARG_LOG_LEVEL)) {
        auto log_level = get_arg_value(AgentArgs::ARG_LOG_LEVEL);
        if (!is_valid_log_level(log_level)) {
            err_msg = "Log level \"" + log_level + "\" is not recognized value, using default log level!";
            return JNI_ERR;
        } else {
          return JNI_OK;
        }
    }else{
        return JNI_OK;
    }
}

int AgentArgs::check_for_mandatory_args(std::string &err_msg) {
    if (args.find(ARG_INSTRUMENTOR_JAR) == args.end()) {
        err_msg = "Mandatory argument \"" + ARG_INSTRUMENTOR_JAR + "=<path>\" missing, stopping the agent!";
        return JNI_ERR;
    }

    if (args.find(ARG_SOCKET_ADDRESS) == args.end()) {
        err_msg = "Mandatory argument \"" + ARG_SOCKET_ADDRESS + "=<path>\" missing, stopping the agent!";
        return JNI_ERR;
    }
    return JNI_OK;
}


int AgentArgs::parse_args(std::string options, std::string &err_msg) {
    // we cannot use logging in this method since some arguments may alter the logging sybsystem
    // ( log level, log dir ). Therefore we parse arguments and returned the message in a parameter which then can be
    // logged using the properly set up logger

    // first split to arguments pairs
    std::vector<std::string> pairs;
    boost::split(pairs, options, boost::is_any_of(";"), boost::token_compress_on);
    for (int i = 0; i < pairs.size(); i++) {
        // Skip the empty pairs. For example, empty string is added to pairs vector of options ends with ;
        if (!pairs[i].empty()) {
            std::vector<std::string> arg_split;
            boost::split(arg_split, pairs[i], boost::is_any_of("="));

            if (arg_split.size() != 2) {
                // it means the argument line does not match the pattern name1=value1;name2=value2
                err_msg = "Wrong argument pair:" + pairs[i] + ", arguments have to have format name=value";
                return JNI_ERR;
            } else {
                auto previous = args.insert({arg_split[0], arg_split[1]});
                if (!previous.second) {
                    err_msg = "Argument " + arg_split[0] + " is already defined. Arguments can be defined only once!";
                    return JNI_ERR;
                }
            }
        }
    }

    if (check_for_mandatory_args(err_msg) == JNI_ERR) {
        return JNI_ERR;
    }

    if(validate_log_level(err_msg) == JNI_ERR){
        return JNI_ERR;
    }

    return JNI_OK;
}

