//
// Created by Jakub Háva on 01/06/16.
//

#include <string>
#include <boost/algorithm/string.hpp>
#include <jni.h>
#include <boost/filesystem/path.hpp>
#include <boost/filesystem.hpp>
#include <regex>
#include "AgentArgs.h"
#include "Logging.h"

using namespace Distrace;
using namespace Distrace::Logging;

// instantiate argument names
const std::string AgentArgs::ARG_INSTRUMENTOR_SERVER_JAR = "instrumentor_server_jar";
const std::string AgentArgs::ARG_INSTRUMENTOR_SERVER_CP = "instrumentor_server_cp";
const std::string AgentArgs::ARG_INSTRUMENTOR_LIB_JAR = "instrumentor_lib_jar";
const std::string AgentArgs::ARG_INSTRUMENTOR_MAIN_CLASS = "instrumentor_main_class";
const std::string AgentArgs::ARG_CONNECTION_STR = "connection_str";

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

void AgentArgs::connection_str_to_nanomsg_addr(){
    std::string connection_str = get_arg_value(ARG_CONNECTION_STR);
    // this function expects already validated connection_str
    if(connection_str=="ipc"){
        connection_str = "ipc://"+ boost::filesystem::unique_path().string();
    }else{
        connection_str = "tcp://"+connection_str;
    }
    args[ARG_CONNECTION_STR] = connection_str;
}

int AgentArgs::validate_connection_str(std::string &err_msg){
    if(is_arg_set(ARG_CONNECTION_STR)){
        std::string value = get_arg_value(ARG_CONNECTION_STR);

        if(value == "ipc"){
            return JNI_OK;
        }else{
            // the other possible format is ip:port
            std::regex re("^(.*):\\d{1,5}");
            if(std::regex_match(value.begin(),value.end(),re))
            {
                return JNI_OK;
            }else{
                err_msg = "Communication type \"" + value + "\" is not recognized value!";
                return JNI_ERR;
            }
        }
    }
    return JNI_OK;
}

int AgentArgs::validate_log_level(std::string &err_msg){
    if (is_arg_set(ARG_LOG_LEVEL)) {
        auto log_level = get_arg_value(ARG_LOG_LEVEL);
        if (!is_valid_log_level(log_level)) {
            err_msg = "Log level \"" + log_level + "\" is not recognized value!";
            return JNI_ERR;
        } else {
            return JNI_OK;
        }
    }
    return JNI_OK;
}

int AgentArgs::validate_args(std::string &err_msg){
    if(validate_log_level(err_msg) == JNI_ERR){
        return JNI_ERR;
    }

    if(validate_connection_str(err_msg) == JNI_ERR){
        return JNI_ERR;
    }

    return JNI_OK;
}

int AgentArgs::check_for_mandatory_arg(std::string arg_name, std::string &err_msg){
    if (args.find(arg_name) == args.end()) {
        err_msg = "Mandatory argument \"" + arg_name + "\" is missing, stopping the agent!";
        return JNI_ERR;
    }
    return JNI_OK;
}

int AgentArgs::check_for_mandatory_args(std::string &err_msg) {
    if(check_for_mandatory_arg(ARG_INSTRUMENTOR_LIB_JAR, err_msg) == JNI_ERR){
        return JNI_ERR;
    }

    if(!is_arg_set(ARG_CONNECTION_STR) || (is_arg_set(ARG_CONNECTION_STR) && get_arg_value(ARG_CONNECTION_STR) == "ipc")){
        if(check_for_mandatory_arg(ARG_INSTRUMENTOR_MAIN_CLASS, err_msg) == JNI_ERR){
            err_msg = ARG_INSTRUMENTOR_MAIN_CLASS+" is missing, it has to be set when starting in local mode!";
            return JNI_ERR;
        }

        if(check_for_mandatory_arg(ARG_INSTRUMENTOR_MAIN_CLASS, err_msg) == JNI_ERR){
            err_msg = ARG_INSTRUMENTOR_SERVER_JAR+" is missing, it has to be set when starting in local mode!";
            return JNI_ERR;
        }
    }

    return JNI_OK;
}

void AgentArgs::fill_missing_with_defaults(){
    if(!is_arg_set(ARG_LOG_LEVEL)){
        args.insert({ARG_LOG_LEVEL, "error"});
    }

    if(!is_arg_set(ARG_LOG_DIR)){
        boost::filesystem::path full_current_path(boost::filesystem::current_path());
        args.insert({ARG_LOG_DIR, full_current_path.string()});
    }

    if(!is_arg_set(ARG_INSTRUMENTOR_SERVER_CP)){
        args.insert({ARG_INSTRUMENTOR_SERVER_CP, ""});
    }

    if(!is_arg_set(ARG_CONNECTION_STR)){
        args.insert({ARG_CONNECTION_STR, "ipc"});
    }
}

int AgentArgs::parse_args(std::string options, std::string &err_msg) {
    // we cannot use logging in this method since some arguments may alter the logging sybsystem
    // ( log level, log dir ). Therefore we parse arguments and returned the message in a parameter which then can be
    // logged using the properly set up logger

    // first split to arguments pairs
    std::vector<std::string> pairs;
    boost::split(pairs, options, boost::is_any_of(";"), boost::token_compress_on);
    for(int i = 0; i < pairs.size(); i++) {
        // Skip the empty pairs. For example, empty string is added to pairs vector of options ends with ;
        if (!pairs[i].empty()) {
            std::vector<std::string> arg_split;
            boost::split(arg_split, pairs[i], boost::is_any_of("="));

            if(arg_split.size() != 2) {
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

    if(check_for_mandatory_args(err_msg) == JNI_ERR) {
        return JNI_ERR;
    }

    if(validate_args(err_msg) == JNI_ERR){
        return JNI_ERR;
    }

    fill_missing_with_defaults();
    connection_str_to_nanomsg_addr();

    return JNI_OK;
}

bool AgentArgs::is_running_in_local_mode(){
    return boost::starts_with(get_arg_value(ARG_CONNECTION_STR),"ipc");
}
