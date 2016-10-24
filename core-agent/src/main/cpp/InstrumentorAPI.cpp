//
// Created by Jakub Háva on 13/05/16.
//



#include <nnxx/nn.h>
#include <nnxx/message.h>
#include <jni.h>
#include <nnxx/pair.h>
#include <boost/filesystem.hpp>
#include <boost/algorithm/string.hpp>
#include "InstrumentorAPI.h"
#include "Utils.h"
#include "Agent.h"
#include "AgentUtils.h"
#include "JavaUtils.h"
#include <boost/algorithm/string.hpp>


using namespace Distrace;
using namespace Distrace::Logging;
using namespace Distrace::Utils;

byte InstrumentorAPI::REQ_TYPE_INSTRUMENT = 0;
byte InstrumentorAPI::REQ_TYPE_STOP = 1;
byte InstrumentorAPI::REQ_TYPE_CHECK_HAS_CLASS = 2;
byte InstrumentorAPI::REQ_TYPE_REGISTER_BYTECODE = 3;
std::string InstrumentorAPI::ACK_REQ_MSG = "ack_req_msg";
std::string InstrumentorAPI::ACK_REQ_INST_YES = "ack_req_int_yes";
std::string InstrumentorAPI::ACK_REQ_INST_NO = "ack_req_int_no";
std::string InstrumentorAPI::ACK_REQ_AUX_CLASSES = "auxiliary_types";
std::mutex InstrumentorAPI::mtx;           // mutex for critical section


void InstrumentorAPI::add_sent_class(std::string name){
    sent.push_back(name);
}

bool InstrumentorAPI::was_sent(std::string name){
    return std::find(sent.begin(), sent.end(), name) != sent.end();
}
void InstrumentorAPI::add_aux_class(std::string name){
    aux_classes.push_back(name);
}

bool InstrumentorAPI::is_aux_class(std::string name){

    std::string class_name(name);
    std::replace(class_name.begin(), class_name.end(), '/', '.');
    return std::find(aux_classes.begin(), aux_classes.end(), class_name) != aux_classes.end();
}

void InstrumentorAPI::assert_bytes_sent(int numBytesSent, size_t original_len) {
    if (numBytesSent < 0) {
        log(LOGGER_INSTRUMENTOR_API)->error() << "Bytes couldn't be send, error:" << strerror(errno);
    }
    assert(numBytesSent == original_len);
}

int InstrumentorAPI::send_string_request(std::string data) {
    auto originalLen = data.length();
    int numBytesSent = socket.send<std::string>(data);
    assert_bytes_sent(numBytesSent, originalLen);
    return numBytesSent;
}

int InstrumentorAPI::send_byte_arr_request(const byte *input_data, int input_data_len) {
    auto numBytesSent = socket.send(input_data, input_data_len, 0);
    assert_bytes_sent(numBytesSent, input_data_len);
    return numBytesSent;
}


std::string InstrumentorAPI::receive_string_reply() {
    // wait for reply
    return socket.recv<std::string>(0);
}

int InstrumentorAPI::receive_byte_arr_reply(byte **output_buff, int expected_length) {
    return socket.recv(*output_buff, expected_length, 0);
}

std::string InstrumentorAPI::send_and_receive(std::string data) {
    send_string_request(data);
    return receive_string_reply();
}

int InstrumentorAPI::send_and_receive(const byte *input_data, int input_data_len, byte **output_buff) {
    send_byte_arr_request(input_data, input_data_len);
    auto length_as_string = receive_string_reply();
    auto expected_length = std::atoi(length_as_string.c_str());
    *output_buff = (byte *) malloc(sizeof(byte) * expected_length);
    return receive_byte_arr_reply(output_buff, expected_length);
}

int InstrumentorAPI::send_byte_request(byte data) {
    byte *buf = (byte *) nn_allocmsg(1, 0);
    buf[0] = data;
    int numBytesSent = socket.send((void *) buf, 1, 0);
    assert_bytes_sent(numBytesSent, 1);
    return numBytesSent;
}

std::string InstrumentorAPI::send_and_receive(byte data) {
    send_byte_request(data);
    return receive_string_reply();
}

void InstrumentorAPI::send_req_type(byte req_type) {
    auto reply = send_and_receive(req_type);
    assert(reply == ACK_REQ_MSG);
}


void InstrumentorAPI::load_aux_classes(std::string class_name){

    log(LOGGER_INSTRUMENTOR_API)->info() << "Loading auxiliary classes for " << class_name;

    auto reply = receive_string_reply();
    while(reply == ACK_REQ_AUX_CLASSES){
        // keep loading them
        auto aux_class_name = receive_string_reply();
        auto length_as_string = receive_string_reply();
        auto expected_length = std::atoi(length_as_string.c_str());

        byte* output_buffer = (byte *) malloc(sizeof(byte) * expected_length);
        receive_byte_arr_reply(&output_buffer, expected_length);
        log(LOGGER_INSTRUMENTOR_API)->info() << "Receive bytecode for auxiliary class " << aux_class_name;


        std::vector<std::string> tokens;
        boost::split(tokens, aux_class_name, boost::is_any_of("."), boost::token_compress_on);
        auto class_file_name = tokens.back() + ".class";
        tokens.pop_back();

        std::string sep(1, boost::filesystem::path::preferred_separator);
        auto class_path = boost::algorithm::join(tokens, sep);
        auto path = path_to_dir_with_aux_classes + class_path + sep;
        auto fully_path = path + class_file_name;

        boost::filesystem::create_directories(path);
        FILE* file = fopen(fully_path.c_str(), "wb" );
        if(file!=NULL){
            log(LOGGER_INSTRUMENTOR_API)->error() << "Writing to file " + fully_path;

            fwrite(output_buffer, sizeof(output_buffer[0]), expected_length, file);
            fclose(file);
        }else{
            log(LOGGER_INSTRUMENTOR_API)->error() << "Error opening the file " + fully_path;
        }

        add_aux_class(aux_class_name);
        reply = receive_string_reply();
    }

}

bool InstrumentorAPI::should_instrument(std::string class_name) {
    // critical section. Communication started from different threads would break nanomsg
    mtx.lock();
    log(LOGGER_INSTRUMENTOR_API)->info() << "Asking Instrumentor whether it needs to instrument class \"" <<
    class_name << "\"";
    send_req_type(REQ_TYPE_INSTRUMENT);

    // send class name
    send_string_request(class_name);
    // send bytecode
    bool ret_value = false;
    load_aux_classes(class_name);
    auto reply = receive_string_reply();
    if (reply == ACK_REQ_INST_YES) {
        log(LOGGER_INSTRUMENTOR_API)->info() << "Instrumentor reply: Class \"" << class_name <<
        "\" will be instrumented.";
        ret_value = true;
    } else if (reply == ACK_REQ_INST_NO) {
        log(LOGGER_INSTRUMENTOR_API)->info() << "Instrumentor reply: Class \"" << class_name <<
        "\" won't be instrumented.";
    } else {
        // never can be here
        log(LOGGER_INSTRUMENTOR_API)->info() << "Got unexpected reply in should_instrument method : " << reply;
        assert(false);
    }
    mtx.unlock();
    return ret_value;

}

void InstrumentorAPI::send_byte_code(std::string name, const unsigned char *class_data, int data_len){
    mtx.lock();
    send_req_type(REQ_TYPE_REGISTER_BYTECODE);
    send_string_request(name);
    send_byte_arr_request(class_data, data_len);
    mtx.unlock();
}

int InstrumentorAPI::instrument(byte **output_buffer) {
    // fill the output_buffer with the new bytecode and return it's length
    auto length_as_string = receive_string_reply();
    auto expected_length = std::atoi(length_as_string.c_str());
    *output_buffer = (byte *) malloc(sizeof(byte) * expected_length);
    return receive_byte_arr_reply(output_buffer, expected_length);
}

void InstrumentorAPI::stop() {
    // in case of local mode ( IPC communication) delete the file used for the communication
    log(LOGGER_INSTRUMENTOR_API)->info() << "Stopping the instrumentor JVM";
    // remove ipc://, the remaining part represents file ( when running on linux )
    std::string file = Agent::getArgs()->get_arg_value(AgentArgs::ARG_CONNECTION_STR).substr(6);
    boost::filesystem::path file_to_delete(file);
    boost::filesystem::remove(file_to_delete);

    send_req_type(REQ_TYPE_STOP);
}

int InstrumentorAPI::init() {
    const std::string connection_str = Agent::getArgs()->get_arg_value(AgentArgs::ARG_CONNECTION_STR);

    // launch Instrumentor JVM only in case of ipc, when tcp is set, the instrumentor JVM should be already running.
    if(Agent::getArgs()->is_running_in_local_mode()){
        // fork instrumentor JVM
        if (!system(NULL)) {
            log(LOGGER_INSTRUMENTOR_API)->error() << "Can't fork Instrumentor JVM, shell not available!";
            return JNI_ERR;
        }
        const std::string instrumentor_server_jar = Agent::getArgs()->get_arg_value(AgentArgs::ARG_INSTRUMENTOR_SERVER_JAR);
        const std::string instrumentor_main_class = Agent::getArgs()->get_arg_value(AgentArgs::ARG_INSTRUMENTOR_MAIN_CLASS);
        const std::string instrumentor_server_cp = Agent::getArgs()->get_arg_value(AgentArgs::ARG_INSTRUMENTOR_SERVER_CP);
        const std::string log_level = Agent::getArgs()->get_arg_value(AgentArgs::ARG_LOG_LEVEL);
        const std::string log_dir = Agent::getArgs()->get_arg_value(AgentArgs::ARG_LOG_DIR);

        std::string launch_command =
                "java -cp " + instrumentor_server_jar + ":" + instrumentor_server_cp + " " + instrumentor_main_class + " " + connection_str + " " +
                log_level + " " + log_dir  + " & ";
        log(LOGGER_INSTRUMENTOR_API)->info() << "Starting Instrumentor JVM with the command: " << launch_command;
        int result = system(stringToCharPointer(launch_command));
        if (result < 0) {
            log(LOGGER_INSTRUMENTOR_API)->error() << "Instrumentor JVM couldn't be forked because of error:" <<
            strerror(errno);
            return JNI_ERR;
        }
    }{
        //TODO: check the connection
    }
    // create socket which is used to connect to the Instrumentor JVM
    nnxx::socket socket{nnxx::SP, nnxx::PAIR};

    int endpoint = socket.connect(connection_str);
    if (endpoint < 0) {
        log(LOGGER_INSTRUMENTOR_API)->error() << "Returned error code " << errno <<
        ". Connection to the instrumentor JVM can't be established! Is instrumentor JVM running ?";
        return JNI_ERR;
    } else {
        log(LOGGER_INSTRUMENTOR_API)->info() <<
        "Connection to the instrumentor JVM established via IPC. Assigned endpoint ID is " << endpoint;
    }

    Agent::globalData->inst_api = new InstrumentorAPI(std::move(socket));

    // add instrumentor libraries jar on the classpath so our jvm can see created interceptors
    const std::string instrumentor_lib_jar = Agent::getArgs()->get_arg_value(AgentArgs::ARG_INSTRUMENTOR_LIB_JAR);
    jvmtiError error = Agent::globalData->jvmti->AddToSystemClassLoaderSearch(instrumentor_lib_jar.c_str());

    auto ret = AgentUtils::check_jvmti_error(Agent::globalData->jvmti, error,
                                  "Path: " + instrumentor_lib_jar +
                                  " successfully added on the system's classloader search path",
                                  "Cannot add path " + instrumentor_lib_jar +
                                  " on the system's classloader search path");
    if(ret == JNI_ERR){
        return JNI_ERR;
    }

    error = Agent::globalData->jvmti->AddToBootstrapClassLoaderSearch(instrumentor_lib_jar.c_str());
    return AgentUtils::check_jvmti_error(Agent::globalData->jvmti, error,
                                         "Path: " + instrumentor_lib_jar +
                                         " successfully added on the bootstrap's classloader search path",
                                         "Cannot add path " + instrumentor_lib_jar +
                                         " on the bootstraps's classloader search path");

}

InstrumentorAPI::InstrumentorAPI(nnxx::socket socket) {
    this->socket = std::move(socket);
    this->path_to_dir_with_aux_classes = Utils::unique_tmp_dir_path();
    log(LOGGER_INSTRUMENTOR_API)->info() << "Adding directory for auxiliary classes to classpath " << this->path_to_dir_with_aux_classes;
    Agent::globalData->jvmti->AddToSystemClassLoaderSearch(this->path_to_dir_with_aux_classes.c_str());
}

bool InstrumentorAPI::has_class(std::string class_name){
    mtx.lock();
    send_req_type(REQ_TYPE_CHECK_HAS_CLASS);
    auto ret = send_and_receive(class_name);
    mtx.unlock();
    return  ret == "yes";
}