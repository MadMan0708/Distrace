//
// Created by Jakub Háva on 14/04/16.
//

#include <iomanip>
#include <sstream>
#include "Logging.h"

namespace Distrace{
    namespace Logging{

        // List of loggers to create. Add new logger name also here in order to add create new logger.
        std::vector<std::string> loggersToInitialize = {
                LOGGER_AGENT,
                LOGGER_INSTRUMENTOR_API,
                LOGGER_AGENT_CALLBACKS
        };

        namespace {
            std::vector<spdlog::sink_ptr> createSinks() {
                std::vector<spdlog::sink_ptr> sinks;
                // setup asynchronous logging
                size_t q_size = 1048576; //queue size must be power of 2
                spdlog::set_async_mode(q_size);
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

        void registerLoggers() {
            auto sinks = createSinks();

            for (auto logger_name : loggersToInitialize) {
                auto logger = std::make_shared<spdlog::logger>(logger_name, begin(sinks), end(sinks));
                spdlog::register_logger(logger);
            }
        }
    }
}