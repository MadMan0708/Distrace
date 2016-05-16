package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.transformers.SimpleTransformer;

class Instrumentor {

    public static void main(String[] args){
        if(args.length !=1){
            System.out.println("Missing argument socket address");
            System.exit(-1);
        }
        System.out.println("Running forked JVM");

        TransformersManager.transformers.put("cz.cuni.mff.d3s.distrace.examples.Test", new SimpleTransformer());
        new InstrumentorServer(args[0]).start();
    }
}