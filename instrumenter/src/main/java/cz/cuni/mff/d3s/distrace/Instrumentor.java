package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.instrumentation.MainAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.InstrumentorServer;
import cz.cuni.mff.d3s.distrace.utils.InstrumentorConfFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

/**
 * Main entry point for the instrumentation. The extensions build on top of this library are using this class for
 * registering the classes for instrumentation.
 */
public class Instrumentor {

    /**
     * This method has to be called in the extended instrumentation server in order to start the server.
     * Usually before this method is called, the programmer needs to register all classes which should be instrumented
     * using the provided builder
     *
     * @param args    command line arguments of the instrumentor
     * @param builder builder for the instrumentation
     */
    public void start(String[] args, MainAgentBuilder builder) {
        assert args.length == 4;
        // instrumentation server from native agent is always started with the
        // following 4 arguments:
        // - socket address
        // - log level
        // - log dir
        // - class output directory of auxiliary and instrumented classes

        String socketAddress = args[0];

        // when starting instrumentation server externally, it make only sense to pass it ip:port as connection string
        if (!socketAddress.startsWith("tcp") && !socketAddress.startsWith("ipc")) {
            socketAddress = "tcp://" + socketAddress;
        }

        String logLevel = args[1];
        String logDir = args[2];
        String classOutputDir = args[3];
        ConfigurationFactory.setConfigurationFactory(new InstrumentorConfFactory(logLevel, logDir));
        Logger log = LogManager.getLogger(Instrumentor.class);
        log.info("Running forked JVM \n" +
                "   connection string : " + socketAddress + "\n" +
                "   log level         : " + logLevel + "\n" +
                "   log dir           : " + logDir + "\n" +
                "   class output dir  : " + classOutputDir + "\n" +
                "");

        new InstrumentorServer(socketAddress, builder, classOutputDir)
                .start();

    }

}