//
// Created by Jakub Háva on 13/05/16.
//

#ifndef DISTRACE_AGENT_CORE_INSTRUMENTORAPI_H
#define DISTRACE_AGENT_CORE_INSTRUMENTORAPI_H

#include <nnxx/socket.h>
#include "utils/Logging.h"
#include <string>
#include <set>

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
         * Sends class data to the instrumentor JVM. The caller might check if the instrumentor already contains the class
         * data in order to prevent extra sending. This method returns number of sent bytes.
         */
        int sendClassData(std::string className, const unsigned char *classData, int classDataLen);

        /**
         * Load dependencies for the class stored in class_data using the specified class loader
         */
        void loadDependencies(JNIEnv *jni, std::string className, jobject loader, const unsigned char *classData, jint classDataLen);

        /**
         * Checks whether the class is available on the instrumentor. First the local cache is queried and if the
         * class hasn't been found in the local cache of processed classes, the instrumentor JVM is queried
         */
        bool isClassOnInstrumentor(std::string className);

        /**
         * This method sends bytecode to the instrumentor JVM and receives back the instrumented bytecode
         */

        /**
         * Instrument class and set the output class_data and class_data_len pointers
         */
        void instrument(JNIEnv *jni, jobject loader, std::string name, unsigned char **newClassData, jint *newClassDataLen);

        /**
         * Load initializers for specified class
         */
        void loadInitializersForClass(JNIEnv *jni, jclass clazz, std::string className);

        /**
         * This method initializes the instrumentor JVM and return JNI_OK in case of success and JNI_ERR otherwise
         */
        static int init();

        /**
         * Inform the instrumentor JVM that the monitored application has been stopped.
         */
        void stop();

        /**
         * Returns true if the class name is class name which may be considered for instrumentation and also class loader
         * is not ignored class loader for which we don't want to instrument classes
         */
        bool shouldContinue(std::string className, std::string loaderName);

        /**
         * Load helper classes from instrumentor JVM
         */
        void loadPrepClasses();
    private:

        std::map<jint, std::set<std::string>> loadedInterceptorsPerCl;
        /**
         * Map storing name of the instrumented classes as keys and loaded type initializers as their values
         */
        std::map<std::string, std::vector<std::pair<unsigned char *, int>>> initializers;

        /**
         * Map storing name of the interceptors as keys and interceptors bytecode as their values
         */
        std::map<std::string, std::pair<unsigned char *, int>> interceptors;

        /** Mutes which is used to lock pieces of code which communicates with the instrumentor JVM. The
         * communication is using nanomsg framework and communication originating from different thread can cause
         * problems
         */
        static std::mutex mtx;

        /** socket representing the connection to the instrumentor JVM */
        nnxx::socket socket;

        /**
         * Directory where we store helper classes on disk. This is temporary directory deleted when the native agent
         * is stopped
         */
        std::string pathToClassDir;

        /**
         * List of packages' prefixes for which we don't want to instrument classes
         */
        static std::vector<std::string> ignoredPackages;

        /**
         *  The list of class loaders for which we don't want to instrument classes loaded by these class loaders
         */

        static std::vector<std::string> ignoredLoaders;

        /**
         * List of classes we don't want to instrument. These are helper classes sent from Instrumentor JVM,
         * auxiliary classes and interceptor classes
         */
        std::set<std::string> ignoredClasses;

        /**
         * List of sent classes ( bytecode has been sent to instrumentor JVM )
         * We keep this list so we can minimize number of request to the instrumentor
         */
        std::set<std::string> instrumentorClassesCache;

        /**
         * Requests types
         *
         */
        /** Request used to inform the instrumentor current class should be instrumented  */
        static byte REQ_TYPE_INSTRUMENT;
        /** Request used to inform the instrumentor that  that the monitored JVM is being stopped */
        static byte REQ_TYPE_STOP;
        /** Request used to question the instrumentor whether it has the class available or not */
        static byte REQ_TYPE_CHECK_HAS_CLASS;
        /** Request used to signal that the bytecode will be just stored on the instrumentor without instrumenting it */
        static byte REQ_TYPE_REGISTER_BYTECODE;
        /** Request used to signal that we are loading helper classes required in the native agent */
        static byte REQ_TYPE_HELPER_CLASSES;

        /**
         * Acknowledgement types
         */
        /** Acknowledgement for receiving general message */
        static std::string ACK_REQ_MSG;
        /** Acknowledgement saying that class should be instrumented */
        static std::string ACK_REQ_INST_YES;
        /** Acknowledgement saying that class should not be instrumented */
        static std::string ACK_REQ_INST_NO;
        /** Acknowledgement saying that there are more auxiliary classes for current class */
        static std::string ACK_REQ_AUX_CLASSES;
        /** Acknowledgement saying that a class is already available on the instrumentor */
        static std::string ACK_REQ_CLASS_ON_INSTRUMENTOR;
        /** Acknowledgement saying that there are more initializers for current class */
        static std::string ACK_REQ_INITIALIZERS;

        /**
         * Get serialized initializers for a specified class name
         */
        std::vector<std::pair<unsigned char *, int>> getInitializersFor(std::string className);

        /**
         * Save class on disk into specific directory where the class loader can see it
         */
        void saveClassOnDisk(std::string className, byte* classBytes, int classBytesLength);

        /**
        * This method sends request to the instrumentor JVM which decides whether this class should be instrumented
        * or not. If this class is about to be instrumented this method also receives bytecode for all auxiliary classes.
        */
        bool shouldInstrument(JNIEnv *jni, jobject loader, std::string className);

        /**
         * Loads all auxiliary classes generated by byte-buddy
         */
        void loadAuxiliaryClasses(std::string className);

        /**
         * Load all initializers generated by byte-buddy
         */
        void loadInitializers(JNIEnv *jni, jobject loader, std::string className);

        /**
         * Receive and load interceptor class
         */
        void loadInterceptor(JNIEnv *jni, jobject loader, std::string className);

        /**
         * Sends referenced class to the instrumentor JVM only if the instrumentor JVM already doesn't contain the bytecode for
         * this class. The referenced class is such a class which is another class super class, interface, field or
         * argument or return value of a method.
         */
        int sendReferencedClass(JNIEnv *jni, std::string className, jobject loader, const unsigned char **classData);

        /**
         * Checks whether the the class is in local cache saying whether the class is available on the instrumentor or
         * not
         */
        bool isInInstrumentorClassesCache(std::string className);

        /**
         * Cache the class name locally so we know it is already available on the instrumentor
         */
        void putToInstrumentorClassesCache(std::string className);


        /**
         * Cache whether this class is ignored - should not be instrumented
         */
        bool isIgnoredClass(std::string className);

        /**
         * Check whether this class is located in a package from which we don't need to send classes to the instrumentor
         */
        bool isInIgnoredPackage(std::string className);

        /**
         * Check whether the specified classloader is ignored. We don't want to instrument classes which have
         * been loaded by ignored class loaders.
         */
        bool isIgnoredClassLoader(std::string classLoader);


        /**
        * Sent data to instrumentor JVM. The data is a arbitrary string.
        */
        int sendStringRequest(std::string data);

        /**
        * Receive message in form of string
        */
        std::string receiveStringReply();

        /**
         * Send data in a form of string and receive reply in a form of string
         */
        std::string sendAndReceive(std::string data);

        /**
         * Send data in a form of single byte to instrumentor JVM
         */
        int sendByteRequest(byte data);

        /**
         * Send data in a form of single byte and receive reply in a form of string
         */
        std::string sendAndReceive(byte data);

        /**
         * Send data to instrumentor JVM. The data is array of bytes.
         */
        int sendByteArrayRequest(const byte *inputData, int inputDataLen);

        /**
         * Receive message in a form of byte array.
         */
        int receiveByteArrayReply(byte **inputBuffer, int expectedLength);


        /**
         * Send data in a form of byte array and receive reply in a form of byte array.
         * The parameters are input byte array, the input array length and the last
         * parameter is byte array to which the reply is stored. This method returns the length of returned byte array.
         */
        int sendAndReceive(const byte *inputData, int inputDataLen, byte **outputBuffer);

        /**
         * Send the request type to instrumentor JVM and validates that instrumentor JVM successfully received the
         * message.
         */
        void sendRequestAndAssertReply(byte requestType);

        /**
         * Assert that all bytes have been successfully sent
         */
        void assertBytesSent(int numBytesSent, size_t original_len);

        /**
         * Receive int reply. The sender sends data as string and we convert it to integer
         */
        int receiveIntReply();
    };
}


#endif //DISTRACE_AGENT_CORE_INSTRUMENTORAPI_H
