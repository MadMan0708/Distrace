//
// Created by Jakub Háva on 13/05/16.
//

#ifndef DISTRACE_AGENT_CORE_INSTRUMENTORAPI_H
#define DISTRACE_AGENT_CORE_INSTRUMENTORAPI_H

#include <nnxx/socket.h>
#include "Logging.h"
#include <string>

namespace Distrace {
    /**
     * Byte in JVM is represented to native agent as unsigned char
     */
    typedef unsigned char byte;

    /**
     * This class encapsulates all the communication with the instrumentor JAR
     */
    class InstrumentorAPI {
    public:

        /**
         * Constructor which creates instance of this class based on the socket connection to the instrumentor JVM
         */
        InstrumentorAPI(nnxx::socket socket);

        /**
         * This method send request to the instrumentor JVM which decides whether this class should be instrumented
         * or not.
         */
        bool should_instrument(std::string class_name);

        /**
         * This method sent bytecode to the instrumentor JVM and receives back the instrumented bytecode
         */
        int instrument(const byte* input_data, int input_data_len, byte** output_buffer);

        /**
         * This method initializes the instrumentor JAR and return JNI_OK in case of success and JNI_ERR otherwise
         */
        static int init();

        /**
         * Inform the instrumentor JVM that the monitored JVM has been stopped
         */
        void stop();
    private:
        /** Request type for class instrumentation */
        static byte REQ_TYPE_INSTRUMENT;
        /** Request type informing the instrumentor JVM that the monitored JVM is being stopped */
        static byte REQ_TYPE_STOP;
        /** Acknowledgement for receiving general message */
        static std::string ACK_REQ_MSG;
        /** Acknowledgement saying that class should be instrumented */
        static std::string ACK_REQ_INST_YES;
        /** Acknowledgement saying that class should not be instrumented */
        static std::string ACK_REQ_INST_NO;

        /** Mutes which is used to lock pieces of code which communicates with the instrumentor JAR. The
         * communication is using nanomsg framework and communication originating from different thread can cause
         * problems */
        static std::mutex mtx;

        /** socket representing the connection to the instrumentor JVM */
        nnxx::socket socket;

        /**
         * Assert that all bytes has been successfully sent
         */
        void assert_bytes_sent(int numBytesSent, size_t original_len);

        /**
         * Sent data to instrumentor JVM. The data is a arbitrary string.
         */
        int send_string_request(std::string data);

        /**
         * Send data to instrumentor JVM. The data is just one single byte.
         */
        int send_byte_request(byte data);

        /**
         * Send data to instrumentor JVM. The data is array of bytes.
         */
        int send_byte_arr_request(const byte *data, int data_len);

        /**
         * Receive message in form of string
         */
        std::string receive_string_reply();

        /**
         * Receive message in a form of byte array.
         */
        int receive_byte_arr_reply(byte** buff, int expected_length);

        /**
         * Send the request type to instrumentor JVM and validates that instrumentor JVM successfully received the
         * message
         */
        void send_req_type(byte req_type);

        /**
         * Send data in a form of single byte and receive reply in a form of string
         */
        std::string send_and_receive(byte data);

        /**
         * Send data in a form of string and receive reply in a form of string
         */
        std::string send_and_receive(std::string data);

        /**
         * Send data in a form of byte array and receive reply in a form of byte array.
         * The parameters are input byte array, the input array length and the last
         * parameter is byte array to which the reply is stored. This method return the length of returned byte array.
         */
        int send_and_receive(const byte *input_data, int input_data_len, byte** output_buff);
    };
}


#endif //DISTRACE_AGENT_CORE_INSTRUMENTORAPI_H
