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
find_package(nanomsg REQUIRED) # nanomsg library
find_package(nanomsgxx REQUIRED) # nanomsg binding for c++

INCLUDE_DIRECTORIES("include")
# include all necessary directories
include_directories( ${JNI_INCLUDE_DIRS} )
include_directories( ${Boost_INCLUDE_DIRS})
include_directories( ${nanomsg_INCLUDE_DIRS})
include_directories( ${nanomsgxx_INCLUDE_DIRS})

# specify source files
set(SOURCE_FILES src/main/cpp/AgentUtils.cpp src/main/cpp/AgentUtils.h src/main/cpp/AgentCallbacks.cpp src/main/cpp/AgentCallbacks.h src/main/cpp/Agent.cpp src/main/cpp/Agent.h src/main/cpp/Logging.cpp src/main/cpp/Logging.h src/main/cpp/Utils.cpp src/main/cpp/Utils.h src/main/cpp/JavaUtils.cpp src/main/cpp/JavaUtils.h src/main/cpp/InstrumentorAPI.cpp src/main/cpp/InstrumentorAPI.h src/main/cpp/AgentArgs.cpp src/main/cpp/AgentArgs.h)


# create a shared library
add_library("-${PROJECT_NAME}" SHARED ${SOURCE_FILES})
target_link_libraries("-${PROJECT_NAME}" ${NANOMSG_LIBRARY})
target_link_libraries("-${PROJECT_NAME}" ${NANOMSGXX_LIBRARY})