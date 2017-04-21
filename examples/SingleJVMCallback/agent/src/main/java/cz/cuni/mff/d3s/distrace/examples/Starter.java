package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.CustomAgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Starter of the instrumentation server. This example demonstrates monitoring of callbacks inside one JVM.
 * It combines both advices and interceptors.
 */
public class Starter {
    public static void main(String[] args) {
        new Instrumentor().start(args,
                new CustomAgentBuilder() {
                    @Override
                    public AgentBuilder createAgent(BaseAgentBuilder builder) {
                        return builder
                                .type(is(Task.class))
                                .transform(new TaskTransformer())
                                .type(is(CallbackCreator.class))
                                .transform(new CallBackCreatorTransformer())
                                .type(isSubTypeOf(Callback.class).and(not(is(Callback.class))))
                                .transform(new CallbackTransformer())
                                .type(is(Executor.class))
                                .transform(new ExecutorTransformer());
                    }
                });

    }

}
