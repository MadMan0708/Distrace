package cz.cuni.mff.d3s.distrace;

import net.bytebuddy.agent.builder.AgentBuilder;

import java.util.HashMap;

public class TransformersManager {

    public static HashMap<String, AgentBuilder.Transformer> transformers = new HashMap<String, AgentBuilder.Transformer>();

}
