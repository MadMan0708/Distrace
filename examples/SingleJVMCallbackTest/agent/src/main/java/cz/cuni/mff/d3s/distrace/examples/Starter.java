package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;

/**
 * Starter of instrumentor
 */
public class Starter {
    public static void main(String[] args){
        new Instrumentor()
               // .addTransformer("cz.cuni.mff.d3s.distrace.examples.CallbackCreator", new CallBackCreatorTransformer())
               // .addTransformer("cz.cuni.mff.d3s.distrace.examples.Callback", new CallbackTransformer())
                .start(args);
    }

}
