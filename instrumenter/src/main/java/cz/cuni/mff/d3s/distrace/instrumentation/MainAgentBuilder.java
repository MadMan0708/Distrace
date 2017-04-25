package cz.cuni.mff.d3s.distrace.instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;

/**
 * Main Agent Builder in the application.
 * <p>
 * All the instrumentation points are defined as part of this builder inside the createAgent method.
 */
public abstract class MainAgentBuilder {

    /**
     * Method where all the instrumentation points are defined when the instrumentation server is extended
     *
     * @param builder                   base agent builder for the instrumentation
     * @param pathToHelperClasses path where native agent stores some received from the instrumentation
     *                                  server or some additional helper instrumented classes.
     * @return prepared builder
     */
    public abstract AgentBuilder createAgent(BaseAgentBuilder builder, String pathToHelperClasses);
}
