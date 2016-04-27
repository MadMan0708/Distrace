cmake_minimum_required(VERSION 3.4)
project(distrace-agent-core)

# set variables
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -pthread")
set(AGENT_CORE_BUILD_DIR "${CMAKE_CURRENT_SOURCE_DIR}/build")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${AGENT_CORE_BUILD_DIR}")
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake/Modules/")

# load required modules
find_package(JNI REQUIRED) # JNI libraries
find_package(BOOST REQUIRED) #  Boost libraries
find_package(ZMQ REQUIRED) # ZeroMQ library
find_package(AZMQ REQUIRED) # C++11 binding for ZeroMQ library
# Include header only libraries in the include dir
INCLUDE_DIRECTORIES("include")
# include all necessary directories
include_directories( ${JNI_INCLUDE_DIRS} )
include_directories( ${Boost_INCLUDE_DIRS})
include_directories( ${ZMQ_INCLUDE_DIRS})
include_directories( ${AZMQ_INCLUDE_DIRS})

# specify source files
set(SOURCE_FILES src/main/cpp/AgentUtils.cpp src/main/cpp/AgentUtils.h src/main/cpp/AgentCallbacks.cpp src/main/cpp/AgentCallbacks.h src/main/cpp/Agent.cpp src/main/cpp/Agent.h src/main/cpp/Logger.cpp src/main/cpp/Logger.h src/main/cpp/Utils.cpp src/main/cpp/Utils.h)

# create a shared library
add_library("-${PROJECT_NAME}" SHARED ${SOURCE_FILES})