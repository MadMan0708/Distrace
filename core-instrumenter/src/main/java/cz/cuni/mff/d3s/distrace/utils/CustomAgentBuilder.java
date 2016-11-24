package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.agent.builder.AgentBuilder;

/**
 * Created by kuba on 06/09/16.
 */
public abstract class CustomAgentBuilder {

    public abstract AgentBuilder createAgent(BaseAgentBuilder builder, String pathToGeneratedClasses);
}
