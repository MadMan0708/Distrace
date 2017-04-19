//
// Created by Jakub Háva on 01/06/16.
//

#include <string>
#include <jni.h>
#include <boost/filesystem/path.hpp>
#include <boost/filesystem.hpp>
#include <regex>
#include <iostream>
#include <fstream>
#include "AgentArgs.h"
#include "utils/Logging.h"
#include "utils/Utils.h"

using namespace Distrace;
using namespace Distrace::Logging;
namespace fs = boost::filesystem;
// instantiate argument names
const std::string AgentArgs::ARG_INSTRUMENTOR_SERVER_JAR = "instrumentor_server_jar";
const std::string AgentArgs::ARG_INSTRUMENTOR_SERVER_CP = "instrumentor_server_cp";
const std::string AgentArgs::ARG_INSTRUMENTOR_MAIN_CLASS = "instrumentor_main_class";
const std::string AgentArgs::ARG_CONNECTION_STR = "connection_str";
const std::string AgentArgs::ARG_LOG_LEVEL = "log_level";
const std::string AgentArgs::ARG_LOG_DIR = "log_dir";
const std::string AgentArgs::ARG_SAVER_TYPE = "saver";
const std::string AgentArgs::ARG_CONFIG_FILE = "config_file";
const std::string AgentArgs::ARG_CLASS_OUTPUT_DIR = "class_output_dir";

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
        fs::path ipcPath = fs::path(Utils::createUniqueTempDir()) / boost::filesystem::unique_path();
        connectionStr = "ipc://" + ipcPath.string();
    }else{
        connectionStr = "tcp://" + connectionStr;
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
            if(!std::regex_match(value.begin(), value.end(), re)) {
                errorMsg = "Communication type \"" + value + "\" is not recognized value!";
                return JNI_ERR;
            }
        }
    }
    return JNI_OK;
}

int AgentArgs::validateLogLevel(std::string &errorMsg){
    if(isArgSet(ARG_LOG_LEVEL)){
        auto logLevel = getArgValue(ARG_LOG_LEVEL);
        if (!isValidLogLevel(logLevel)) {
            errorMsg = "Log level \"" + logLevel + "\" is not recognized value!";
            return JNI_ERR;
        } else {
            return JNI_OK;
        }
    }
    return JNI_OK;
}

int AgentArgs::validateSaverType(std::string &errorMsg){
    if(isArgSet(ARG_SAVER_TYPE)){
        auto saverType = getArgValue(ARG_SAVER_TYPE);
        if(Utils::startsWith(saverType, "directZipkin")){
            std::regex re("^directZipkin\\(.*:\\d{1,5}\\)");
            if(!std::regex_match(saverType.begin(), saverType.end(), re)){
                errorMsg = "Wrong format of directZipkin saver type \"" + saverType + "\". It should be specified as directZipkin(ip:port)";
                return JNI_ERR;
            }
        }else if(Utils::startsWith(saverType, "disk")){
            std::regex re("^disk\\(.*\\)");
            if(!std::regex_match(saverType.begin(), saverType.end(), re)){
                errorMsg = "Wrong format of disk saver type \"" + saverType + "\". It should be specified as disk(path)";
                return JNI_ERR;
            }
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

    if(validateSaverType(err_msg) == JNI_ERR){
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

    if(!isArgSet(ARG_SAVER_TYPE)){
        // the default saver is directly to Zipkin where we expect that zipkin is running on localhost on default port
        args.insert({ARG_SAVER_TYPE, "directZipkin(localhost:9411)"});
    }

    if(!isArgSet(ARG_CLASS_OUTPUT_DIR)){
        args.insert({ARG_CLASS_OUTPUT_DIR, Utils::createUniqueTempDir()});
    }
}

int AgentArgs::splitArgumentPair(std::string pair, std::string &errorMsg){
    // Skip the empty pairs. For example, empty string is added to pairs vector of options ends with ;
    if (!pair.empty()) {
        std::vector<std::string> splits = Utils::splitString(pair, "=");
        if (splits.size() != 2) {
            // it means the argument line does not match the pattern name1=value1;name2=value2
            errorMsg = "Wrong argument pair:" + pair + ", arguments have to have format name=value";
            return JNI_ERR;
        } else {
            auto previous = args.insert({splits[0], splits[1]});
            if (!previous.second) {
                errorMsg = "Argument " + splits[0] + " is already defined. Every argument can be defined only once. In case of the same argument is "
                                                             "set on the command line and in the config file the command line argument has the precedence !";
                return JNI_ERR;
            }
        }
    }
    return JNI_OK;
}
int AgentArgs::parseCLI(std::string options, std::string &errorMsg){
    // first split to arguments pairs
    std::vector<std::string> pairs = Utils::splitString(options, ";", Utils::token_compress_on);

    for(int i = 0; i < pairs.size(); i++) {
        if(splitArgumentPair(pairs[i], errorMsg) == JNI_ERR){
            return JNI_ERR;
        }
    }
    return JNI_OK;
}

int AgentArgs::parseConfigFile(std::string &errorMsg){
    if(isArgSet(ARG_CONFIG_FILE)){
        // load config file into options string
        std::ifstream file(getArgValue(ARG_CONFIG_FILE));
        std::string line;
        while (std::getline(file, line))
        {
            if(Utils::splitString(line, "=")[0] == ARG_CONFIG_FILE){
                errorMsg = "config_file option can not be set inside the configuration file!";
                return JNI_ERR;
            }

            if(splitArgumentPair(line, errorMsg) == JNI_ERR){
                return JNI_ERR;
            }
        }
    }
    return JNI_OK;
}
int AgentArgs::parseArgs(std::string options, std::string &errorMsg) {
    // we cannot use logging in this method since some arguments may alter the logging sybsystem
    // ( log level, log dir ). Therefore we parse arguments and return the message in a inout argument which can be
    // logged using the properly set up logger

    if(parseCLI(options, errorMsg) == JNI_ERR){
        return JNI_ERR;
    }

    // process options defined in config file
    if(parseConfigFile(errorMsg) == JNI_ERR){
        return JNI_ERR;
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
    return Utils::startsWith(getArgValue(ARG_CONNECTION_STR), "ipc");
}
