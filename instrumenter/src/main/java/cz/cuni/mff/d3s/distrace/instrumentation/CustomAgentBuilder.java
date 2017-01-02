package cz.cuni.mff.d3s.distrace.instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;

public abstract class CustomAgentBuilder {

    public abstract AgentBuilder createAgent(BaseAgentBuilder builder, String pathToGeneratedClasses);
}
