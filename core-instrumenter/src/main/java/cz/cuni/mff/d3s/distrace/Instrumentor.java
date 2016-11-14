package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.InstrumentorConfFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.reflections.Reflections;

import java.util.Set;

public class Instrumentor {
    private static Logger log;

    /**
     * This method has to be called in a custom implementation of Instrumentor in order to start the Instrumentor.
     * Usually before this method is called the programmer should register all classes which should be instrumented
     * using TransformerManager
     * @param args command line arguments of the instrumentor
     */
    public void start(String[] args, CustomAgentBuilder builder){
        assert args.length == 3; // we always start Instrumentor from native agent and 3 parameters should be
        // always passed to it
        // - socket address
        // - log level
        // - log dir

        String socketAddress = args[0];

        // when starting Instrumentor server externally, it make only sense to pass it ip:port as connection string
        if(!socketAddress.startsWith("tcp") && !socketAddress.startsWith("ipc")){
            socketAddress = "tcp://"+socketAddress;
        }

        String logLevel = args[1];
        String logDir = args[2];
        ConfigurationFactory.setConfigurationFactory(new InstrumentorConfFactory(logLevel, logDir));
        log = LogManager.getLogger(Instrumentor.class);
        log.info("Running forked JVM \n" +
                "   connection string : " + socketAddress + "\n" +
                "   log level         : " + logLevel + "\n" +
                "   log dir           : " + logDir + "\n" +
                "");

        new InstrumentorServer(socketAddress, builder)
                .start();

    }


}