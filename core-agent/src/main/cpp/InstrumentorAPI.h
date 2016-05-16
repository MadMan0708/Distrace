//
// Created by Jakub Háva on 13/05/16.
//

#ifndef DISTRACE_AGENT_CORE_INSTRUMENTORAPI_H
#define DISTRACE_AGENT_CORE_INSTRUMENTORAPI_H

#include <nnxx/socket.h>
#include "Logging.h"
#include <string>

namespace Distrace {

    typedef unsigned char byte;

    class InstrumentorAPI {
    public:
        static byte REQ_TYPE_INSTRUMENT;
        static byte REQ_TYPE_STOP;
        static std::string ACK_REQ_MSG;
        static std::string ACK_REQ_INST_YES;
        static std::string ACK_REQ_INST_NO;
        InstrumentorAPI(nnxx::socket socket);
        bool should_instrument(std::string class_name);
        int instrument(const byte* input_data, int input_data_len, byte** output_buffer);
        static int init(std::string path_to_instrumentor_jar);
        void stop();
    private:
        static std::mutex mtx;
        nnxx::socket socket;

        void assert_bytes_sent(int numBytesSent, size_t original_len);
        int send_string_request(std::string data);
        int send_byte_request(byte data);

        std::string receive_msg_reply();
        int receive_byte_arr_reply(byte** buff, int expected_length);
        int send_byte_arr_request(const byte *data, int data_len);
        void send_req_type(byte req_type);
        std::string send_and_receive(byte data);
        std::string send_and_receive(std::string data);
        int send_and_receive(const byte *input_data, int input_data_len, byte** output_buff);
    };
}


#endif //DISTRACE_AGENT_CORE_INSTRUMENTORAPI_H
