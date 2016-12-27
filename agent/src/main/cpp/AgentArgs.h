//
// Created by Jakub Háva on 01/06/16.
//

#ifndef DISTRACE_AGENT_CORE_AGENTPARAMETERS_H
#define DISTRACE_AGENT_CORE_AGENTPARAMETERS_H

#include <string>
#include <map>

namespace Distrace {

    /**
     * This class represents arguments which can be passed to the native agent
     */
    class AgentArgs {

    public:
        static const std::string ARG_INSTRUMENTOR_SERVER_JAR;
        static const std::string ARG_INSTRUMENTOR_SERVER_CP;
        static const std::string ARG_INSTRUMENTOR_MAIN_CLASS;
        static const std::string ARG_CONNECTION_STR;
        static const std::string ARG_LOG_LEVEL;
        static const std::string ARG_LOG_DIR;
        static const std::string ARG_SAVER_TYPE;
        static const std::string ARG_CONFIG_FILE;

        /**
         * Get the internal arguments map
         */
        std::map<std::string, std::string> getArgsMap();

        /**
         * Get argument value and fail if the argument value is not set.
         */
        std::string getArgValue(std::string argName);

        /**
         * Check if the arguments is set
         */
        bool isArgSet(std::string argName);

        /**
         * Parse the arguments and store the parsed result in this class' internal map
         *
         * This method takes two arguments -  options string to be parsed and err_msg which is filled
         * with the error message in case of problem during the parsing
         */
        int parseArgs(std::string options, std::string &errorMsg);

        /**
         * Return true if we run in IPC mode ( local mode ), false otherwise
         */
        bool isRunningInLocalMode();

    private:
        /**
         * Internal arguments holder where key = arg name, value = arg value
         */
        std::map<std::string, std::string> args;

        /**
         * Split argument pair into argument name and argument value and perform check whether the argument
         * has already been set
         */
        int splitArgumentPair(std::string pair, std::string &errorMsg);
        /**
         * Split and set arguments passed on cli without any further checks for validity
         */
        int parseCLI(std::string options, std::string &errorMsg);

        /**
        * Split and set arguments passed via config file without any further checks for validity
        */
        int parseConfigFile(std::string &errorMsg);

        /**
         * Fill missing arguments with missing values
         */
        void fillMissingWithDefaults();

        /**
         * Validate the arguments
         */
        int validateArgs(std::string &errorMsg);

        /**
         * Validates connection_str argument in case it is set
         */
        int validateConnectionStr(std::string &errorMsg);

        /**
         * Validates log_level argument in case it is set
         */
        int validateLogLevel(std::string &errorMsg);

        /**
         * Validates saver argument in case it is set
         */
        int validateSaverType(std::string &errorMsg);
        /**
         * Check for single mandatory argument
         */
        int checkForMandatoryArg(std::string arg_name, std::string &errorMsg);

        /**
         * Replace Distrace connection string in arguments by nanomsg address in the arguments
         */
        void connectionStrToNanomsgAddr();

        /**
         * Check for mandatory arguments and in case of error fills err_msg with the error message which
         * can be further logged out
         */
        int checkForMandatoryArgs(std::string &errorMsg);
    };

}

#endif //DISTRACE_AGENT_CORE_AGENTPARAMETERS_H
