//
// Created by Jakub Háva on 14/04/16.
//

#include <spdlog/spdlog.h>
#include <iomanip>
#include <sstream>
#include "Logger.h"

namespace DistraceAgent {

    std::vector<spdlog::sink_ptr> Logger::sinks;
    bool Logger::isInitialized = false;

    void Logger::initLogger(){
        // setup asynchronous logging
        size_t q_size = 1048576; //queue size must be power of 2
        spdlog::set_async_mode(q_size);

        // one sink to print logs on the console
        sinks.push_back(std::make_shared<spdlog::sinks::stdout_sink_st>());

        auto t = std::time(nullptr);
        auto tm = *std::localtime(&t);
        std::stringstream file_name_buffer;
        file_name_buffer << "distrace_" << std::put_time(&tm,"%d_%m_%Y:%H_%M_%S") << ".log";

        sinks.push_back(std::make_shared<spdlog::sinks::simple_file_sink_st>(file_name_buffer.str(), true));
        isInitialized = true;
    }

    std::shared_ptr<spdlog::logger> Logger::getLogger(std::string className){
        if(!isInitialized){
            initLogger();
        }
        auto combined_logger = std::make_shared<spdlog::logger>(className, begin(sinks), end(sinks));
        spdlog::register_logger(combined_logger);
        return combined_logger;
    }

}