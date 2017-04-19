package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.CustomAgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * Starter of the instrumentation server. This example is used to demonstrate tracing on simple communication between
 * the threads on the same node. Advice API is used in this example for the instrumentation.
 */
public class Starter {
    public static void main(String[] args) {
        new Instrumentor().start(args,
                new CustomAgentBuilder() {
                    @Override
                    public AgentBuilder createAgent(BaseAgentBuilder builder) {
                        return builder
                                .type(named("cz.cuni.mff.d3s.distrace.examples.StarterTask"))
                                .transform(new StarterTaskTransformer())
                                .type(named("cz.cuni.mff.d3s.distrace.examples.DependantTask"))
                                .transform(new DependantTaskTransformer());
                    }
                });
    }

}
