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
#include "utils/Logging.h"
#include "utils/Utils.h"


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

std::string AgentArgs::getArgValue(std::string argName) {
    if(isArgSet(argName)){
        return args.find(argName)->second;
    }else{
        throw std::runtime_error("Argument \"" + argName + "\" is not set. Before getting the value check if it's set using the is_arg_set method");
    }
}

bool AgentArgs::isArgSet(std::string argName) {
     return args.count(argName) != 0;
}

void AgentArgs::connectionStrToNanomsgAddr(){
    std::string connectionStr = getArgValue(ARG_CONNECTION_STR);
    // this function expects already validated connectionStr
    if(connectionStr=="ipc"){
        connectionStr = "ipc://"+ Utils::createUniqueTempDir()+boost::filesystem::unique_path().string();
    }else{
        connectionStr = "tcp://"+connectionStr;
    }
    args[ARG_CONNECTION_STR] = connectionStr;
}

int AgentArgs::validateConnectionStr(std::string &errorMsg){
    if(isArgSet(ARG_CONNECTION_STR)){
        std::string value = getArgValue(ARG_CONNECTION_STR);

        if(value == "ipc"){
            return JNI_OK;
        }else{
            // the other possible format is ip:port
            std::regex re("^(.*):\\d{1,5}");
            if(std::regex_match(value.begin(),value.end(),re))
            {
                return JNI_OK;
            }else{
                errorMsg = "Communication type \"" + value + "\" is not recognized value!";
                return JNI_ERR;
            }
        }
    }
    return JNI_OK;
}

int AgentArgs::validateLogLevel(std::string &errorMsg){
    if (isArgSet(ARG_LOG_LEVEL)) {
        auto log_level = getArgValue(ARG_LOG_LEVEL);
        if (!is_valid_log_level(log_level)) {
            errorMsg = "Log level \"" + log_level + "\" is not recognized value!";
            return JNI_ERR;
        } else {
            return JNI_OK;
        }
    }
    return JNI_OK;
}

int AgentArgs::validateArgs(std::string &err_msg){
    if(validateLogLevel(err_msg) == JNI_ERR){
        return JNI_ERR;
    }

    if(validateConnectionStr(err_msg) == JNI_ERR){
        return JNI_ERR;
    }

    return JNI_OK;
}

int AgentArgs::checkForMandatoryArg(std::string arg_name, std::string &err_msg){
    if (args.find(arg_name) == args.end()) {
        err_msg = "Mandatory argument \"" + arg_name + "\" is missing, stopping the agent!";
        return JNI_ERR;
    }
    return JNI_OK;
}

int AgentArgs::checkForMandatoryArgs(std::string &err_msg) {
    if(checkForMandatoryArg(ARG_INSTRUMENTOR_LIB_JAR, err_msg) == JNI_ERR){
        return JNI_ERR;
    }

    if(!isArgSet(ARG_CONNECTION_STR) || (isArgSet(ARG_CONNECTION_STR) && getArgValue(ARG_CONNECTION_STR) == "ipc")){
        if(checkForMandatoryArg(ARG_INSTRUMENTOR_MAIN_CLASS, err_msg) == JNI_ERR){
            err_msg = ARG_INSTRUMENTOR_MAIN_CLASS+" is missing, it has to be set when starting in local mode!";
            return JNI_ERR;
        }

        if(checkForMandatoryArg(ARG_INSTRUMENTOR_MAIN_CLASS, err_msg) == JNI_ERR){
            err_msg = ARG_INSTRUMENTOR_SERVER_JAR+" is missing, it has to be set when starting in local mode!";
            return JNI_ERR;
        }
    }

    return JNI_OK;
}

void AgentArgs::fillMissingWithDefaults(){
    if(!isArgSet(ARG_LOG_LEVEL)){
        args.insert({ARG_LOG_LEVEL, "error"});
    }

    if(!isArgSet(ARG_LOG_DIR)){
        boost::filesystem::path full_current_path(boost::filesystem::current_path());
        args.insert({ARG_LOG_DIR, full_current_path.string()});
    }

    if(!isArgSet(ARG_INSTRUMENTOR_SERVER_CP)){
        args.insert({ARG_INSTRUMENTOR_SERVER_CP, ""});
    }

    if(!isArgSet(ARG_CONNECTION_STR)){
        args.insert({ARG_CONNECTION_STR, "ipc"});
    }
}

int AgentArgs::parse_args(std::string options, std::string &errorMsg) {
    // we cannot use logging in this method since some arguments may alter the logging sybsystem
    // ( log level, log dir ). Therefore we parse arguments and return the message in a inout argument which can be
    // logged using the properly set up logger

    // first split to arguments pairs
    std::vector<std::string> pairs;
    boost::split(pairs, options, boost::is_any_of(";"), boost::token_compress_on);
    for(int i = 0; i < pairs.size(); i++) {
        // Skip the empty pairs. For example, empty string is added to pairs vector of options ends with ;
        if (!pairs[i].empty()) {
            std::vector<std::string> splits;
            boost::split(splits, pairs[i], boost::is_any_of("="));

            if(splits.size() != 2) {
                // it means the argument line does not match the pattern name1=value1;name2=value2
                errorMsg = "Wrong argument pair:" + pairs[i] + ", arguments have to have format name=value";
                return JNI_ERR;
            } else {
                auto previous = args.insert({splits[0], splits[1]});
                if (!previous.second) {
                    errorMsg = "Argument " + splits[0] + " is already defined. Arguments can be defined only once!";
                    return JNI_ERR;
                }
            }
        }
    }

    if(checkForMandatoryArgs(errorMsg) == JNI_ERR) {
        return JNI_ERR;
    }

    if(validateArgs(errorMsg) == JNI_ERR){
        return JNI_ERR;
    }

    fillMissingWithDefaults();
    connectionStrToNanomsgAddr();
    return JNI_OK;
}

bool AgentArgs::isRunningInLocalMode(){
    return boost::starts_with(getArgValue(ARG_CONNECTION_STR), "ipc");
}
