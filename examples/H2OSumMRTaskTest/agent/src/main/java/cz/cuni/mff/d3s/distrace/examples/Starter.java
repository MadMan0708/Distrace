package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder;

public class Starter {
    public static void main(String args[]){
        new Instrumentor().start(args, new CustomAgentBuilder() {
            @Override
            public AgentBuilder createAgent(BaseAgentBuilder builder) {
                return builder.getAgentBuilder();
            }
        });
    }
}