package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.InstrumentorClassLoader;
import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import nanomsg.exceptions.IOException;
import nanomsg.pair.PairSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class InstrumentorServer {
    private static final Logger log = LogManager.getLogger(InstrumentorServer.class);
    public static HashMap<String, Boolean> classAvailabilityMap = new HashMap<>();
    private static final HashMap<String, byte[]> byteCodeCache = new HashMap<>();
    private static final byte REQ_TYPE_INSTRUMENT = 0;
    private static final byte REQ_TYPE_STOP = 1;
    private static final byte REQ_TYPE_CHECK_HAS_CLASS = 2;
    private static final byte REQ_TYPE_REGISTER_BYTECODE = 3;
    private PairSocket sock;
    private String sockAddr;
    private ClassFileTransformer transformer;
    private CustomAgentBuilder builder;
    private InstrumentorClassLoader instLoader = new InstrumentorClassLoader();

    InstrumentorServer(String sockAddr, CustomAgentBuilder builder) {
        this.sockAddr = sockAddr;
        this.builder = builder;
    }


    private void handleRegisterByteCode(){
        byte[] classNameSlashes = sock.recvBytes();
        String classNameDots = Utils.convertToJavaName(new String(classNameSlashes, StandardCharsets.UTF_8));

       // byte[] byteCode = sock.recvBytes();
        log.info("Registering bytecode for class " + classNameDots );
        //instLoader.registerByteCode(classNameDots, byteCode);
    }
    private void handleHasClassCheck() {
        byte[] classNameSlashes = sock.recvBytes();
        String classNameDots = Utils.convertToJavaName(new String(classNameSlashes, StandardCharsets.UTF_8));


        log.info("Checking whether class is available " + classNameDots);

        // first look into cache
        if(byteCodeCache.containsKey(classNameDots)){
            log.info("Instrumentor contains instrumented bytecode in cache for class " + classNameDots);
            sock.send("yes");
        }else {
            // if the instrumented class is not in the cache, look if we can load it
            try {
                // use current classloader for this check, not InstrumentorClassLoader since that
                // would cause unwanted behaviour and other necessary checks (
                this.getClass().getClassLoader().loadClass(classNameDots);
                log.info("Instrumentor contains class " + classNameDots);
                classAvailabilityMap.put(classNameDots, true);
                sock.send("yes");
            } catch (ClassNotFoundException e) {
                log.info("Instrumentor does not contain class " + classNameDots);
                classAvailabilityMap.put(classNameDots, false);
                sock.send("no");
            }
        }
    }

    private void handleInstrument() {
        byte[] classNameSlashes = sock.recvBytes();
        String classNameDots = Utils.convertToJavaName(new String(classNameSlashes, StandardCharsets.UTF_8));

        // first look into cache and send the instrumented bytecode to the native agent
        if(byteCodeCache.containsKey(classNameDots)){
            sendByteCodeToAgent(byteCodeCache.get(classNameDots));
        // if the bytecode is not in the case try to load it locally without need to send the original bytecode
        // from native agent
        }else if(classAvailabilityMap.get(classNameDots)){
            byte[] transformed = instrument(classNameDots);
            // save the instrumented bytecode
            byteCodeCache.put(classNameDots, transformed);
            sendByteCodeToAgent(transformed);
        // class is not available, we need to get bytecode from native agent
        }else {
            byte[] bytes = sock.recvBytes();
            log.debug("Received class code from native agent: " + classNameDots);
            instLoader.registerByteCode(classNameDots, bytes);
            log.debug("Registered class bytecode into Instrumentor class loader");
            byte[] transformed = instrument(classNameDots);
            // save the instrumented bytecode
            byteCodeCache.put(classNameDots, transformed);
            sendByteCodeToAgent(transformed);
        }
    }

    private void sendByteCodeToAgent(byte[] transformed){
        if (transformed != null) { // the class was transformed
            sock.send(transformed.length + ""); // send length of instrumented code
            sock.send(transformed); // send instrumented bytecode
        }
    }

    private byte[] instrument(String className) {
        // we do not have to provide bytecode as parameter to transform method since it is fetched when needed by our class file locator
        // implemented using byte code class loader

        // it returns null in case the class shouldn't have been transformed
        byte[] transformed = new byte[0];
        try {
            transformed = transformer.transform(instLoader, className, null, null, null);
        } catch (IllegalClassFormatException e) {
            log.error("Invalid bytecode for class " + className);
            sock.send("ERROR_INVALID_FORMAT");
        }
        return transformed;
    }

    private void handleRequest(byte requestType) {
        switch (requestType) {
            case REQ_TYPE_INSTRUMENT:
                handleInstrument();
                break;
            case REQ_TYPE_CHECK_HAS_CLASS:
                handleHasClassCheck();
                break;
            case REQ_TYPE_REGISTER_BYTECODE:
                handleRegisterByteCode();
                break;
        }
    }

    void start() {
        sock = new PairSocket();
        sock.bind(sockAddr);
        transformer = builder.createAgent(new BaseAgentBuilder(sock, instLoader)).makeRaw();
        //noinspection InfiniteLoopStatement
        while (true) {

            try {
                byte[] requestType = sock.recvBytes();
                assert requestType.length == 1;
                byte req = requestType[0];
                log.debug("Processing request of type " + req);
                sock.send("ack_req_msg"); // confirm receiving of the message
                if (req == REQ_TYPE_STOP) {
                    log.info("Instrumentor JVM is being stopped!");
                    // finish all the work which needs to be done and then stop the Instrumentor
                    break;
                }
                handleRequest(req);
            } catch (IOException e) {
                // nothing to receive, wait
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                    // wake up and continue
                }
            }

        }
        sock.close();
    }


}
