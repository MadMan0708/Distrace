cmake_minimum_required(VERSION 3.4)
project(distrace-agent-core)

# set variables
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -pthread")
set(AGENT_CORE_BUILD_DIR "${CMAKE_CURRENT_SOURCE_DIR}/build")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${AGENT_CORE_BUILD_DIR}")
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake/Modules/")

# load required modules
find_package(JNI REQUIRED) # JNI libraries
find_package(Boost COMPONENTS system filesystem REQUIRED) #  Boost libraries
find_package(nanomsg REQUIRED) # nanomsg library
find_package(nanomsgxx REQUIRED) # nanomsg binding for c++

INCLUDE_DIRECTORIES("include")
# include all necessary directories
include_directories( ${JNI_INCLUDE_DIRS} )
include_directories( ${Boost_INCLUDE_DIRS})
include_directories( ${nanomsg_INCLUDE_DIRS})
include_directories( ${nanomsgxx_INCLUDE_DIRS})

# specify source files
set(SOURCE_FILES src/main/cpp/AgentUtils.cpp src/main/cpp/AgentUtils.h src/main/cpp/AgentCallbacks.cpp src/main/cpp/AgentCallbacks.h src/main/cpp/Agent.cpp src/main/cpp/Agent.h src/main/cpp/Logging.cpp src/main/cpp/Logging.h src/main/cpp/Utils.cpp src/main/cpp/Utils.h src/main/cpp/JavaUtils.cpp src/main/cpp/JavaUtils.h src/main/cpp/InstrumentorAPI.cpp src/main/cpp/InstrumentorAPI.h src/main/cpp/AgentArgs.cpp src/main/cpp/AgentArgs.h src/main/cpp/ByteReader.cpp src/main/cpp/ByteReader.h src/main/cpp/bytecode/ConstantPool.cpp src/main/cpp/bytecode/ConstantPool.h src/main/cpp/bytecode/Constant.cpp src/main/cpp/bytecode/Constant.h src/main/cpp/bytecode/JavaConst.h src/main/cpp/bytecode/ConstantClass.cpp src/main/cpp/bytecode/ConstantClass.h src/main/cpp/bytecode/ConstantFieldref.cpp src/main/cpp/bytecode/ConstantFieldref.h src/main/cpp/bytecode/ConstantCP.cpp src/main/cpp/bytecode/ConstantCP.h src/main/cpp/bytecode/ConstantMethodref.cpp src/main/cpp/bytecode/ConstantMethodref.h src/main/cpp/bytecode/ConstantInterfaceMethodref.cpp src/main/cpp/bytecode/ConstantInterfaceMethodref.h src/main/cpp/bytecode/ConstantString.cpp src/main/cpp/bytecode/ConstantString.h src/main/cpp/bytecode/ConstantInteger.cpp src/main/cpp/bytecode/ConstantInteger.h src/main/cpp/bytecode/ConstantFloat.cpp src/main/cpp/bytecode/ConstantFloat.h src/main/cpp/bytecode/ConstantLong.cpp src/main/cpp/bytecode/ConstantLong.h src/main/cpp/bytecode/ConstantDouble.cpp src/main/cpp/bytecode/ConstantDouble.h src/main/cpp/bytecode/ConstantNameAndType.cpp src/main/cpp/bytecode/ConstantNameAndType.h src/main/cpp/bytecode/ConstantUtf8.cpp src/main/cpp/bytecode/ConstantUtf8.h src/main/cpp/bytecode/ConstantMethodHandle.cpp src/main/cpp/bytecode/ConstantMethodHandle.h src/main/cpp/bytecode/ConstantMethodType.cpp src/main/cpp/bytecode/ConstantMethodType.h src/main/cpp/bytecode/ConstantInvokeDynamic.cpp src/main/cpp/bytecode/ConstantInvokeDynamic.h src/main/cpp/bytecode/Field.cpp src/main/cpp/bytecode/Field.h src/main/cpp/bytecode/Attribute.cpp src/main/cpp/bytecode/Attribute.h src/main/cpp/bytecode/FieldOrMethod.cpp src/main/cpp/bytecode/FieldOrMethod.h src/main/cpp/AccessFlags.cpp src/main/cpp/AccessFlags.h src/main/cpp/bytecode/JavaConst.cpp src/main/cpp/bytecode/Method.cpp src/main/cpp/bytecode/Method.h src/main/cpp/bytecode/ClassParser.cpp src/main/cpp/bytecode/ClassParser.h)


# create a shared library
add_library("-${PROJECT_NAME}" SHARED ${SOURCE_FILES})
target_link_libraries("-${PROJECT_NAME}" ${NANOMSG_LIBRARY})
target_link_libraries("-${PROJECT_NAME}" ${NANOMSGXX_LIBRARY})
target_link_libraries("-${PROJECT_NAME}" ${Boost_FILESYSTEM_LIBRARY} ${Boost_SYSTEM_LIBRARY})