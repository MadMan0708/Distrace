package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.utils.InstrumentorConfFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

public class Instrumentor {
    private static Logger log;

    /**
     * This method has to be called in a custom implementation of Instrumentor in order to start the Instrumentor.
     * Usually before this method is called the programmer should register all classes which should be instrumented
     * using TransformerManager
     * @param args command line arguments
     */
    public static void start(String[] args){
        assert args.length == 3; // we always start Instrumentor from native agent and 3 parameters should be
        // always passed to it - socket address, log level and log dir

        String socketAddress = args[0];
        String logLevel = args[1];
        String logDir = args[2];
        ConfigurationFactory.setConfigurationFactory(new InstrumentorConfFactory(logLevel, logDir));
        log = LogManager.getLogger(Instrumentor.class);
        log.info("Running forked JVM");

        InstrumentorServer server = new InstrumentorServer(socketAddress);
        server.start();
    }

}