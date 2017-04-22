package cz.cuni.mff.d3s.distrace.instrumentation;


import cz.cuni.mff.d3s.distrace.tracing.Span;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import cz.cuni.mff.d3s.distrace.json.*;
import cz.cuni.mff.d3s.distrace.storage.DirectZipkinSaver;
import cz.cuni.mff.d3s.distrace.storage.JSONDiskSaver;
import cz.cuni.mff.d3s.distrace.storage.SpanSaver;
import cz.cuni.mff.d3s.distrace.tracing.TraceContextManager;
import cz.cuni.mff.d3s.distrace.utils.*;
import nanomsg.exceptions.IOException;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.utility.privilege.SetAccessibleAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Instrumentation server used to handle requests from the native agent
 */
public class InstrumentorServer {
    private static final Logger log = LogManager.getLogger(InstrumentorServer.class);
    private static final HashMap<String, byte[]> byteCodeCache = new HashMap<>();
    private static final byte REQ_TYPE_INSTRUMENT = 0;
    private static final byte REQ_TYPE_STOP = 1;
    private static final byte REQ_TYPE_CHECK_HAS_CLASS = 2;
    private static final byte REQ_TYPE_REGISTER_BYTECODE = 3;
    private static final byte REQ_TYPE_PREP_CLASSES = 4;
    private SocketWrapper sock;
    private String sockAddr;
    private ClassFileTransformer transformer;
    private MainAgentBuilder builder;
    private InstrumentorClassLoader instLoader = new InstrumentorClassLoader();
    private String classOutputDir;


    /**
     * Create instrumentation server
     *
     * @param sockAddr       socket address used for communication with the native agent
     * @param builder        main agent builder with the instrumentation points
     * @param classOutputDir directory where some instrumented classes are generated. This
     *                       is mainly used when instrumenting Javassist code.
     */
    public InstrumentorServer(String sockAddr, MainAgentBuilder builder, String classOutputDir) {
        this.sockAddr = sockAddr;
        this.builder = builder;
        this.classOutputDir = classOutputDir;
    }

    /**
     * Start the server and start handling the requests
     */
    public void start() {
        sock = new SocketWrapper(sockAddr);
        transformer = builder.createAgent(new BaseAgentBuilder(sock, instLoader), classOutputDir).makeRaw();
        //noinspection InfiniteLoopStatement
        while (true) {

            try {
                byte[] requestType = sock.receiveBytes();
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
                }
            }
        }
        sock.close();
    }

    private static Class[] helperClasses = {
            Interceptor.class,
            LoadedTypeInitializer.class,
            LoadedTypeInitializer.Compound.class,
            LoadedTypeInitializer.ForStaticField.class,
            SetAccessibleAction.class,
            StorageUtils.class,
            ReflectionUtils.class,
            StackTraceUtils.class,
            TraceContext.class,
            Span.class,
            TraceContextManager.class,
            TraceContext.SpanEvent.class,
            SpanSaver.class,
            DirectZipkinSaver.class,
            JSONDiskSaver.class,
            DirectZipkinSaver.DirectZipkinSaverTask.class,
            JSONDiskSaver.JSONDiskSaverTask.class,
            NativeAgentUtils.class
    };

    private static Class[] jsonClasses = {
            JSON.class,
            JSONArray.class,
            JSONLiteral.class,
            JSONNumber.class,
            JSONObject.class,
            JSONString.class,
            JSONValue.class,
            JSONStringBuilder.class,
            JSONPrettyStringBuilder.class
    };

    private static ArrayList<Class<?>> customSaverClasses = Utils.getCustomSpanSaverClasses();

    /**
     * Register byte code received from the native agent in the instrumentation class loader
     */
    private void handleRegisterByteCode() {
        String classNameSlashes = sock.receiveString();
        String classNameDots = Utils.toNameWithDots(classNameSlashes);

        byte[] byteCode = sock.receiveBytes();
        log.info("Registering bytecode for class " + classNameDots);
        instLoader.registerByteCode(classNameDots, byteCode);
    }

    /**
     * Check whether the instrumentation server already contains bytecode for the class ( original or
     * instrumented ) and notify back the native agent.
     */
    private void handleHasClassCheck() {
        String classNameSlashes = sock.receiveString();
        String classNameDots = Utils.toNameWithDots(classNameSlashes);

        log.info("Checking whether class is available " + classNameDots);

        // first look into cache if the instrumented bytecode is available
        if (byteCodeCache.containsKey(classNameDots)) {
            log.info("Instrumentor contains instrumented bytecode in cache for class " + classNameDots);
            sock.send("yes");
        } else if (instLoader.contains(classNameDots)) {
            // if the instrumented class is not in the cache, look if we can load the original one or
            // if we have the original in the instrumentor loader
            log.info("Instrumentor contains cached bytecode ( not instrumented yet) in cache for class " + classNameDots);
            sock.send("yes");
        } else {
            try {
                // use current classloader for this check, not InstrumentorClassLoader since that
                // would cause unwanted behaviour and other necessary checks (
                this.getClass().getClassLoader().loadClass(classNameDots);
                log.info("Instrumentor contains class " + classNameDots);
                sock.send("yes");
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                log.info("Instrumentor does not contain class " + classNameDots);
                sock.send("no");
            }
        }
    }

    /**
     * Send bytecode for the specified class to the native agent.
     *
     * @param clazz class to send
     * @throws java.io.IOException
     */
    private void sendClazz(Class clazz) throws java.io.IOException {
        byte[] bytes = Utils.getBytesForClass(clazz);
        sock.send(Utils.toNameWithSlashes(clazz.getName()));
        sock.send(bytes.length);
        sock.send(bytes);
    }

    /**
     * Send additional helper classes required by the instrumentation framework which needs to be
     * available at runtime of the monitored application
     */
    private void handleSentPrepClasses() {
        try {
            sock.send(helperClasses.length + customSaverClasses.size() + jsonClasses.length);
            for (Class clazz : helperClasses) {
                sendClazz(clazz);
            }
            for (Class clazz : customSaverClasses) {
                sendClazz(clazz);
            }
            for (Class clazz : jsonClasses) {
                sendClazz(clazz);
            }
        } catch (java.io.IOException ignore) {
            assert false : " Can't never be here since we know this class is available and we know our class loader" +
                    "structure";
        }
    }

    /**
     * Perform the instrumentation and put the instrumented bytecode into the cache so the instrumentation
     * of the same class does not have to be computed again
     */
    private void handleInstrument() {
        String classNameSlashes = sock.receiveString();
        String classNameDots = Utils.toNameWithDots(classNameSlashes);

        // first look into cache and send the instrumented bytecode to the native agent
        if (byteCodeCache.containsKey(classNameDots)) {
            sendByteCodeToAgent(byteCodeCache.get(classNameDots));
            // instrumented class is not available, instrument it
        } else {
            byte[] transformed = instrument(classNameDots);
            // save the instrumented bytecode
            byteCodeCache.put(classNameDots, transformed);
            sendByteCodeToAgent(transformed);
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


    /**
     * Send bytecode to the native agent.
     * @param transformed transformed byte code
     */
    private void sendByteCodeToAgent(byte[] transformed) {
        if (transformed != null) { // the class was transformed
            sock.send(transformed.length); // send length of instrumented code
            sock.send(transformed); // send instrumented bytecode
        }
    }

    /**
     * Dispatcher methods for the incoming requests from the native agent
     * @param requestType request type from the native agent
     */
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
            case REQ_TYPE_PREP_CLASSES:
                handleSentPrepClasses();
                break;
            default:
                throw new RuntimeException("Unknown request type " + requestType);
        }
    }

}
