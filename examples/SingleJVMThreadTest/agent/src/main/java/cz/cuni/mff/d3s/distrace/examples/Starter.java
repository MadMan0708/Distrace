package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.examples.transformers.DependantTaskTransformer;
import cz.cuni.mff.d3s.distrace.examples.transformers.StarterTaskTransformer;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;

/**
 * Starter of instrumentor
 */
public class Starter {
    public static void main(String[] args){
        new Instrumentor()
                .addTransformer("cz.cuni.mff.d3s.distrace.examples.StarterTask", new StarterTaskTransformer())
                .addTransformer("cz.cuni.mff.d3s.distrace.examples.DependantTask", new DependantTaskTransformer())
                .start(args);
    }

}
