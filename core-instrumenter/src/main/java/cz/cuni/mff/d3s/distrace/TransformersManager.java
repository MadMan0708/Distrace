package cz.cuni.mff.d3s.distrace;

import net.bytebuddy.agent.builder.AgentBuilder;

import java.util.HashMap;

public class TransformersManager {

    public TransformersManager(){

    }

    public TransformersManager(HashMap<String, AgentBuilder.Transformer> initialClasses){
        transformers.putAll(initialClasses);
    }

    public void register(String className, AgentBuilder.Transformer transformer){
        transformers.put(className, transformer);
    }

    public  boolean hasClass(String className){
        return transformers.containsKey(className);
    }

    public AgentBuilder.Transformer getTransformerForClass(String className){
        return transformers.get(className);
    }

    private HashMap<String, AgentBuilder.Transformer> transformers = new HashMap<String, AgentBuilder.Transformer>();

}
