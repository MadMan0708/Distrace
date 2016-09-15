package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Starter of instrumentor
 */
public class Starter {
    public static void main(String[] args) {
        new Instrumentor().start(args,
                new CustomAgentBuilder() {
                    @Override
                    public AgentBuilder createAgent(BaseAgentBuilder builder) {
                        return builder
                                .type(nameStartsWith("cz.cuni.mff.d3s.distrace.examples.Callback").and(not(isInterface())))
                                .transform(new CallbackTransformer())
                                .type(named("cz.cuni.mff.d3s.distrace.examples.CallbackCreator"))
                                .transform(new CallBackCreatorTransformer());
                    }
                });

    }

}
