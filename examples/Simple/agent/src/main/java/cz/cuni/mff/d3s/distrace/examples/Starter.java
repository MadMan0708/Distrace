package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.TransformersManager;
import cz.cuni.mff.d3s.distrace.examples.transformers.SimpleTransformer;

/**
 * Starter of instrumentor
 */
public class Starter {
    public static void main(String[] args){
        TransformersManager.register("cz.cuni.mff.d3s.distrace.examples.Printer", new SimpleTransformer());
        Instrumentor.start(args);
    }

}
