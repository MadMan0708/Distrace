package cz.cuni.mff.d3s.distrace;

import nanomsg.exceptions.IOException;
import nanomsg.pair.PairSocket;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.instrument.IllegalClassFormatException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

public class InstrumentorServer {
    private static final Logger log = LogManager.getLogger(InstrumentorServer.class);

    public static final byte REQ_TYPE_INSTRUMENT = 0;
    public static final byte REQ_TYPE_STOP = 1;
    private PairSocket sock;
    private String sockAddr;
    private URL[] class_path_entries;

    public InstrumentorServer(String sockAddr, String classPath) {
        this.sockAddr = sockAddr;

        String[] split = classPath.split(":");
        class_path_entries = new URL[split.length];
        for (int i = 0; i < split.length; i++) {
            try {
                class_path_entries[i] = new File(split[i]).toURI().toURL();
            } catch (MalformedURLException e) {
                // ignore
            }
        }
    }

    public void handleInstrument() {
        try {
            byte[] name = sock.recvBytes();
            String nameString = new String(name, StandardCharsets.UTF_8);
            if (TransformersManager.hasClass(nameString.replace("/", "."))) {
                log.info("Handling instrumentation of class:  " + nameString.replace("/", "."));
                sock.send("ack_req_int_yes");

                byte[] byteCode = sock.recvBytes(); // receive the bytecode to instrument
                log.info("Bytecode+"+ byteCode[0] + " a "+byteCode[1] + " a "+byteCode[2] +" a "+ byteCode[3]);
                byte[] transformedByteCode = instrument(nameString, byteCode);
                sock.send(transformedByteCode.length + ""); // send length of instrumented code
                sock.send(transformedByteCode); // send instrumented bytecode
            } else {
                sock.send("ack_req_int_no");
            }

        } catch (IllegalClassFormatException e) {
            System.out.println("INVALID");
            sock.send("ERROR_INVALID_FORMAT");
        }
    }

    public void handleRequest(byte requestType) {
        switch (requestType) {
            case REQ_TYPE_INSTRUMENT:
                handleInstrument();
                break;
        }
    }

    public void start() {
        sock = new PairSocket();
        sock.bind(sockAddr);
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

    public byte[] instrument(String className, final byte[] bytecode) throws IllegalClassFormatException {
        ClassLoader cl = new URLClassLoader(class_path_entries);
        String nameAsInJava = className.replace("/", ".");
        return new AgentBuilder.Default()
                .with(new AgentBuilder.Listener() {

                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, DynamicType dynamicType) {
                        log.info("Transformed: " + typeDescription + " " + dynamicType);
                    }

                    @Override
                    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                        log.info("Ignored: " + typeDescription);
                    }

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
                        log.error("Error: " + typeName + " " + throwable + ", classloader: " + classLoader.toString());
                    }

                    @Override
                    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module) {
                        log.info("Complete: " + typeName + " " + classLoader);
                    }
                })
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(new AgentBuilder.LocationStrategy.Simple(ClassFileLocator.ForClassLoader.of(cl)))
                .type(ElementMatchers.named(nameAsInJava))
                .transform(TransformersManager.getTransformerForClass(nameAsInJava)).makeRaw().transform(cl, className, null, null, bytecode);
    }

}
