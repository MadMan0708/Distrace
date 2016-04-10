//
// Created by Jakub Háva on 14/04/16.
//

#ifndef DISTRACE_AGENT_CORE_LOGGER_H
#define DISTRACE_AGENT_CORE_LOGGER_H

namespace DistraceAgent {
    class Logger {
    public:
        static std::shared_ptr<spdlog::logger> getLogger(std::string className);

    private:
        static void initLogger();
        static bool isInitialized;
        static std::vector<spdlog::sink_ptr> sinks;
    };
}


#endif //DISTRACE_AGENT_CORE_LOGGER_H
