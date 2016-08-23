package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.examples.transformers.DependantTaskTransformer;
import cz.cuni.mff.d3s.distrace.examples.transformers.StarterTaskTransformer;

/**
 * Starter of instrumentor
 */
public class Starter {
    public static void main(String[] args){
      //  new Instrumentor()
                Instrumentor.addTransformer("cz.cuni.mff.d3s.distrace.examples.StarterTask", new StarterTaskTransformer());
                Instrumentor.addTransformer("cz.cuni.mff.d3s.distrace.examples.DependantTask", new DependantTaskTransformer());
                Instrumentor.start(args);
    }

}
