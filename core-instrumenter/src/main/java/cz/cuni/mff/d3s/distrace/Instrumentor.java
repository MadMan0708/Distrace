package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.transformers.SimpleTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Instrumentor {
    private static final Logger log = LogManager.getLogger(Instrumentor.class);

    public static void main(String[] args){
        if(args.length != 1){
            log.error("Missing argument - socket address");
            System.exit(-1);
        }
        String socketAddress = args[0];
        log.info("Running forked JVM");

        TransformersManager manager = new TransformersManager();
        manager.register("cz.cuni.mff.d3s.distrace.examples.Test", new SimpleTransformer());


        InstrumentorServer server = new InstrumentorServer(socketAddress, manager);
        server.start();
    }
}