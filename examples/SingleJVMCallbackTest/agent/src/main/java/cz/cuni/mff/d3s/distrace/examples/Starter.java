package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.CustomAgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isSubTypeOf;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * Starter of instrumentor
 */
public class Starter {
    public static void main(String[] args) {
        new Instrumentor().start(args,
                new CustomAgentBuilder() {
                    @Override
                    public AgentBuilder createAgent(BaseAgentBuilder builder, String pathToGeneratedClasses) {
                        return builder
                                .type(is(CallbackCreator.class))
                                .transform(new CallBackCreatorTransformer())
                                .type(isSubTypeOf(Callback.class).and(not(is(Callback.class))))
                                .transform(new CallbackTransformer());
                    }
                });

    }

}
