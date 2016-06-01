//
// Created by Jakub Háva on 13/05/16.
//



#include <nnxx/nn.h>
#include <nnxx/message.h>
#include <jni.h>
#include <nnxx/pair.h>
#include "InstrumentorAPI.h"
#include "Utils.h"
#include "Agent.h"
#include "AgentUtils.h"

using namespace Distrace;
using namespace Distrace::Logging;
using namespace Distrace::Utilities;

byte InstrumentorAPI::REQ_TYPE_INSTRUMENT = 0;
byte InstrumentorAPI::REQ_TYPE_STOP = 1;
std::string InstrumentorAPI::ACK_REQ_MSG = "ack_req_msg";
std::string InstrumentorAPI::ACK_REQ_INST_YES = "ack_req_int_yes";
std::string InstrumentorAPI::ACK_REQ_INST_NO = "ack_req_int_no";
std::mutex InstrumentorAPI::mtx;           // mutex for critical section

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

bool InstrumentorAPI::should_instrument(std::string class_name) {
    // critical section. Communication started from different threads would break nanomsg
    mtx.lock();
    log(LOGGER_INSTRUMENTOR_API)->debug() << "Asking Instrumentor whether it needs to instrument class \"" <<
    class_name << "\"";
    send_req_type(REQ_TYPE_INSTRUMENT);

    bool ret_value = false;
    // send class name to the Instrumentor JVM
    std::string reply = send_and_receive(class_name);
    if (reply == ACK_REQ_INST_YES) {
        log(LOGGER_INSTRUMENTOR_API)->info() << "Instrumentor reply: Class \"" << class_name <<
        "\" will be instrumented.";
        ret_value = true;
    } else if (reply == ACK_REQ_INST_NO) {
        log(LOGGER_INSTRUMENTOR_API)->debug() << "Instrumentor reply: Class \"" << class_name <<
        "\" won't be instrumented.";
    } else {
        // never can be here
        assert(false);
    }
    mtx.unlock();
    return ret_value;

}

int InstrumentorAPI::instrument(const byte *input_data, int input_data_len, byte **output_buffer) {
    // fill the output_buffer with the new bytecode and return it's length
    return send_and_receive(input_data, input_data_len, output_buffer);
}

void InstrumentorAPI::stop() {
    log(LOGGER_INSTRUMENTOR_API)->info() << "Stopping the Instrumentor JVM";
    send_req_type(REQ_TYPE_STOP);
}

int InstrumentorAPI::init(std::string path_to_instrumentor_jar) {
    if (!system(NULL)) {
        log(LOGGER_INSTRUMENTOR_API)->error() << "Can't fork Instrumentor JVM, shell not available!";
        return JNI_ERR;
    }

    // fork instrumentor JVM
    const std::string addr = Agent::getArgs()->get_arg_value(AgentArgs::ARG_SOCKET_ADDRESS);
    int result = system(stringToCharPointer("java -jar " + path_to_instrumentor_jar + " " + addr + " & "));
    if (result < 0) {
        log(LOGGER_INSTRUMENTOR_API)->error() << "Instrumentor JVM couldn't be forked because of error:" <<
        strerror(errno);
        return JNI_ERR;
    }
    // create socket which is used to connect to the Instrumentor JVM
    nnxx::socket socket{nnxx::SP, nnxx::PAIR};

    int endpoint = socket.connect(addr);
    if (endpoint < 0) {
        log(LOGGER_INSTRUMENTOR_API)->error() << "Returned error code " << errno <<
        ". Connection to the instrumentor JVM can't be established! Is instrumentor JVM running ?";
        return JNI_ERR;
    } else {
        log(LOGGER_INSTRUMENTOR_API)->info() <<
        "Connection to the instrumentor JVM established via IPC. Assigned endpoint ID is " << endpoint;
    }

    Agent::globalData->inst_api = new InstrumentorAPI(std::move(socket));

    // add instrumentor jar on the classpath so our jvm can see interceptors defined in the Instrumentor JVM
    std::string instrumentor_jar = Agent::getArgs()->get_arg_value(AgentArgs::ARG_INSTRUMENTOR_JAR);
    jvmtiError error = Agent::globalData->jvmti->AddToSystemClassLoaderSearch(instrumentor_jar.c_str());

    return AgentUtils::check_jvmti_error(Agent::globalData->jvmti, error,
                                         "Path: " + instrumentor_jar +
                                         " successfully added on the system's classloader search path",
                                         "Cannot add path " + instrumentor_jar +
                                         " on the system's classloader search path");

}

InstrumentorAPI::InstrumentorAPI(nnxx::socket socket) {
    this->socket = std::move(socket);
}


