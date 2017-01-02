package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.instrumentation.CustomAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.InstrumentorServer;
import cz.cuni.mff.d3s.distrace.utils.InstrumentorConfFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

public class Instrumentor {

    /**
     * This method has to be called in a custom implementation of Instrumentor in order to start the Instrumentor.
     * Usually before this method is called the programmer should register all classes which should be instrumented
     * using TransformerManager
     * @param args command line arguments of the instrumentor
     */
    public void start(String[] args, CustomAgentBuilder builder){
        assert args.length == 4; // we always start Instrumentor from native agent and 4 parameters should be
        // always passed to it
        // - socket address
        // - log level
        // - log dir
        // - path to class dir

        String socketAddress = args[0];

        // when starting Instrumentor server externally, it make only sense to pass it ip:port as connection string
        if(!socketAddress.startsWith("tcp") && !socketAddress.startsWith("ipc")){
            socketAddress = "tcp://"+socketAddress;
        }

        String logLevel = args[1];
        String logDir = args[2];
        String pathToClasses = args[3];
        ConfigurationFactory.setConfigurationFactory(new InstrumentorConfFactory(logLevel, logDir));
        Logger log = LogManager.getLogger(Instrumentor.class);
        log.info("Running forked JVM \n" +
                "   connection string : " + socketAddress + "\n" +
                "   log level         : " + logLevel + "\n" +
                "   log dir           : " + logDir + "\n" +
                "   path to classes   : " + pathToClasses + "\n" +
                "");

        new InstrumentorServer(socketAddress, builder, pathToClasses)
                .start();

    }

}