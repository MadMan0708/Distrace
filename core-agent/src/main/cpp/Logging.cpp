//
// Created by Jakub Háva on 14/04/16.
//

#include <iomanip>
#include <sstream>
#include "Logging.h"

namespace Distrace {
    namespace Logging {

        // Map containing mapping from string to log level
        std::map<std::string, spdlog::level::level_enum> log_levels_map = {
                {"trace",    spdlog::level::level_enum::trace},
                {"debug",    spdlog::level::level_enum::debug},
                {"info",     spdlog::level::level_enum::info},
                {"warn",     spdlog::level::level_enum::warn},
                {"error",    spdlog::level::level_enum::err},
                {"critical", spdlog::level::level_enum::critical},
                {"alert",    spdlog::level::level_enum::alert},
                {"emerg",    spdlog::level::level_enum::emerg},
                {"off",      spdlog::level::level_enum::off}
        };

        // List of loggers to create. Add new logger name also here in order to add create new logger.
        std::vector<std::string> loggers_to_initialize = {
                LOGGER_AGENT,
                LOGGER_INSTRUMENTOR_API,
                LOGGER_AGENT_CALLBACKS
        };

        namespace {
            std::vector<spdlog::sink_ptr> create_sinks() {
                std::vector<spdlog::sink_ptr> sinks;
                // setup asynchronous logging
                size_t q_size = 1048576; //queue size must be power of 2
                spdlog::set_async_mode(q_size);

                spdlog::set_level(spdlog::level::err); // set default log level

                // one sink to print logs on the console
                sinks.push_back(std::make_shared<spdlog::sinks::stdout_sink_mt>());

                auto t = std::time(nullptr);
                auto tm = *std::localtime(&t);
                std::stringstream file_name_buffer;
                file_name_buffer << "distrace_" << std::put_time(&tm, "%d_%m_%Y:%H_%M_%S") << ".log";

                sinks.push_back(std::make_shared<spdlog::sinks::simple_file_sink_mt>(file_name_buffer.str(), true));
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
                assert(false);
            }
        }

        void register_loggers() {
            auto sinks = create_sinks();

            for (auto logger_name : loggers_to_initialize) {
                auto logger = std::make_shared<spdlog::logger>(logger_name, begin(sinks), end(sinks));
                spdlog::register_logger(logger);
            }
        }
    }
}