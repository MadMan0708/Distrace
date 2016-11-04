//
// Created by Jakub Háva on 14/04/16.
//

#include <iomanip>
#include <sstream>
#include <boost/filesystem/path.hpp>
#include <boost/filesystem.hpp>
#include "Logging.h"
#include "../AgentArgs.h"
#include "../Agent.h"

namespace Distrace {
    namespace Logging {

        // Map containing mapping from string to log level. This list of log levels corresponds to log levels
        // used in the instrumentor ( log4j )
        std::map<std::string, spdlog::level::level_enum> log_levels_map = {
                {"trace",    spdlog::level::level_enum::trace},
                {"debug",    spdlog::level::level_enum::debug},
                {"info",     spdlog::level::level_enum::info},
                {"warn",     spdlog::level::level_enum::warn},
                {"error",    spdlog::level::level_enum::err},
                {"fatal",    spdlog::level::level_enum::critical},
                {"off",      spdlog::level::level_enum::off}
        };

        // List of loggers to create. Add new logger name also here in order to add create new logger.
        std::vector<std::string> loggers_to_initialize = {
                LOGGER_AGENT,
                LOGGER_INSTRUMENTOR_API,
                LOGGER_AGENT_CALLBACKS,
                LOGGER_BYTECODE
        };

        namespace {
            std::vector<spdlog::sink_ptr> create_sinks() {
                std::vector<spdlog::sink_ptr> sinks;

                // setup asynchronous logging
                size_t q_size = 1048576; //queue size must be power of 2
                spdlog::set_async_mode(q_size);

                spdlog::set_level(spdlog::level::err); // set default log level

                // sink to print logs on the console
                sinks.push_back(std::make_shared<spdlog::sinks::stdout_sink_mt>());

                // sink to print logs to the file
                std::string  log_dir = Agent::getArgs()->getArgValue(AgentArgs::ARG_LOG_DIR);
                boost::filesystem::path full_current_path(log_dir);
                boost::filesystem::create_directories(full_current_path);

                std::string path_to_log_file = log_dir + boost::filesystem::path::preferred_separator + "distrace_agent.log";

                // create shared log using both sinks created above
                sinks.push_back(std::make_shared<spdlog::sinks::simple_file_sink_mt>(path_to_log_file, true));
                return sinks;
            }
        } // anonymous namespace to hide functions in it


        Logger log(std::string logger_name) {
            return spdlog::get(logger_name);
        }

        bool is_valid_log_level(std::string log_level){
            return log_levels_map.find(log_level) != log_levels_map.end();
        }


        void set_log_level(std::string log_level) {
            if(is_valid_log_level(log_level)){
                spdlog::set_level(log_levels_map.at(log_level));
            }else{
                throw std::runtime_error("Invalid log level: "+ log_level);
            }
        }

        void register_loggers() {
            auto sinks = create_sinks();

            for (auto &logger_name : loggers_to_initialize) {
                auto logger = std::make_shared<spdlog::logger>(logger_name, sinks.begin(), sinks.end());
                spdlog::register_logger(logger);
            }

            auto log_level = Agent::getArgs()->getArgValue(AgentArgs::ARG_LOG_LEVEL);
            set_log_level(log_level);
            log(LOGGER_AGENT)->info("Log level successfully set to: {}.", log_level);
        }
    }
}