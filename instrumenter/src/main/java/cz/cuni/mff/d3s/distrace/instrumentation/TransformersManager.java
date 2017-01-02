package cz.cuni.mff.d3s.distrace.instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;

import java.util.HashMap;

public class TransformersManager {

    public static void register(HashMap<String, AgentBuilder.Transformer> initialClasses){
        transformers.putAll(initialClasses);
    }

    public static void register(String className, AgentBuilder.Transformer transformer){
        transformers.put(className, transformer);
    }

    public static boolean hasClass(String className){
        return transformers.containsKey(className);
    }

    public static AgentBuilder.Transformer getTransformerForClass(String className){
        return transformers.get(className);
    }

    private static HashMap<String, AgentBuilder.Transformer> transformers = new HashMap<String, AgentBuilder.Transformer>();

}
