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

        /**
         * Register the loggers. The loggers can be obtained using getLogger method
         */
        void register_loggers();

        /**
         * Set the log level if the log_level string represents existing log level and returns true, otherwise does
         * not do anything and returns false
         */
        bool set_log_level(std::string log_level);

        /**
         * Get the registered logger using its name
         */
        Logger log(std::string logger_name);

    }
}


#endif //DISTRACE_AGENT_CORE_LOGGER_H
