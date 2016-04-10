cmake_minimum_required(VERSION 3.4)
project(distrace-agent-core)

# set variables
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -pthread")
set(AGENT_CORE_BUILD_DIR "${CMAKE_CURRENT_SOURCE_DIR}/build")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${AGENT_CORE_BUILD_DIR}")
# load required modules
find_package(JNI REQUIRED)

# stop the build if JNI libraries were not found
if(NOT JNI_FOUND)
    message(FATAL_ERROR "JNI Libraries not found")
endif()

# Include header only libraries in the include dir
INCLUDE_DIRECTORIES("include")
# include all necessary directories
include_directories( ${JNI_INCLUDE_DIRS} )

# specify source files
set(SOURCE_FILES src/main/cpp/AgentUtils.cpp src/main/cpp/AgentUtils.h src/main/cpp/AgentCallbacks.cpp src/main/cpp/AgentCallbacks.h src/main/cpp/Agent.cpp src/main/cpp/Agent.h src/main/cpp/Logger.cpp src/main/cpp/Logger.h)

# create a shared library
add_library("-${PROJECT_NAME}" SHARED ${SOURCE_FILES})