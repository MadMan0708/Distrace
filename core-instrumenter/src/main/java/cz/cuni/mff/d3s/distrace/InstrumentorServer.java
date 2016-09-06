package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import nanomsg.exceptions.IOException;
import nanomsg.pair.PairSocket;
import net.bytebuddy.description.type.TypeDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class InstrumentorServer {
    private static final Logger log = LogManager.getLogger(InstrumentorServer.class);
    private static Map<String, TypeDescription> typeDescriptions = new HashMap<>();
    private static final byte REQ_TYPE_INSTRUMENT = 0;
    private static final byte REQ_TYPE_STOP = 1;
    private PairSocket sock;
    private String sockAddr;
    private ClassFileTransformer transformer;
    private CustomAgentBuilder builder;

    InstrumentorServer(String sockAddr, CustomAgentBuilder builder) {
        this.sockAddr = sockAddr;
        this.builder = builder;
    }

    private void handleInstrument() {
        byte[] typeDescriptionAsBytes = sock.recvBytes();

        ByteArrayInputStream bis = new ByteArrayInputStream(typeDescriptionAsBytes);
        TypeDescription typeDescription;
        try {
            typeDescription = (TypeDescription) new ObjectInputStream(bis).readObject();
            byte[] transformedByteCode = instrument(typeDescription);
            sock.send(transformedByteCode.length + ""); // send length of instrumented code
            sock.send(transformedByteCode); // send instrumented bytecode
        } catch (java.io.IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }catch (IllegalClassFormatException e) {
            System.out.println("INVALID");
            sock.send("ERROR_INVALID_FORMAT");
        }
    }

    private void handleRequest(byte requestType) {
        switch (requestType) {
            case REQ_TYPE_INSTRUMENT:
                handleInstrument();
                break;
        }
    }

    void start() {
        sock = new PairSocket();
        sock.bind(sockAddr);
        transformer = builder.createAgent(new BaseAgentBuilder(typeDescriptions, sock)).makeRaw();
        //noinspection InfiniteLoopStatement
        while (true) {

            try {
                byte[] requestType = sock.recvBytes();
                assert requestType.length == 1;
                byte req = requestType[0];
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

    private byte[] instrument(final TypeDescription typeDescription) throws IllegalClassFormatException {
        // register this typeDescription
        typeDescriptions.put(typeDescription.getName(), typeDescription);
        // we do not have to provide bytecode as parameter to transform method since it is fetched when needed by our class file locator
        // implemented using byte code class loader
        return transformer.transform(new URLClassLoader(new URL[0]), typeDescription.getName(), null, null, null);
    }

}
