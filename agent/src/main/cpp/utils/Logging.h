//
// Created by Jakub Háva on 14/04/16.
//

#ifndef DISTRACE_AGENT_CORE_LOGGER_H
#define DISTRACE_AGENT_CORE_LOGGER_H

#include <spdlog/spdlog.h>

namespace Distrace {
    /**
     * This namespace contains logging subsystem for Distrace
     */
    namespace Logging {
        // create shorter alias for logger type
        typedef std::shared_ptr<spdlog::logger> Logger;

        /** Names of loggers */
        const std::string LOGGER_AGENT = "Agent";
        const std::string LOGGER_INSTRUMENTOR_API = "InstrumentorAPI";
        const std::string LOGGER_AGENT_CALLBACKS = "AgentCallbacks";
        const std::string LOGGER_BYTECODE = "ByteCodeProcessing";

        /**
         * Register the loggers. The loggers can be obtained using log method. This method has to be called after
         * after the arguments has been parsed because arguments also contain arguments refining logging.
         */
        void register_loggers();

        /** Checks whether the log_level in string is correct log level */
        bool isValidLogLevel(std::string log_level);

        /**
         * Set the log level if the log_level string represents existing log level and returns true, otherwise does
         * not do anything and returns false
         */
        void set_log_level(std::string log_level);

        /**
         * Get the registered logger using its name
         */
        Logger log(std::string logger_name);

    }
}


#endif //DISTRACE_AGENT_CORE_LOGGER_H
