package cz.cuni.mff.d3s.distrace.examples.SimpleTest;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.TransformersManager;
import cz.cuni.mff.d3s.distrace.examples.SimpleTest.customTransformers.SimpleTransformer;

/**
 * Started of instrumentor
 */
public class Starter {
    public static void main(String[] args){
        TransformersManager.register("cz.cuni.mff.d3s.distrace.examples.SimpleTest.Printer", new SimpleTransformer());
        Instrumentor.start(args);
    }

}
