package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.MainAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.TransformerUtils;
import net.bytebuddy.agent.builder.AgentBuilder;

import static net.bytebuddy.matcher.ElementMatchers.named;


/**
 * Starter of the instrumentation server. This example does not use the user interface however it just demonstrates
 * the instrumentation of dependent classes. Interceptor API is used for instrumentation in this example.
 */
public class Starter {
    public static void main(String[] args) {
        new Instrumentor().start(args,
                new MainAgentBuilder() {
                    @Override
                    public AgentBuilder createAgent(BaseAgentBuilder builder, String pathToInstrumentedClasses) {
                        return builder
                                .type(named("cz.cuni.mff.d3s.distrace.examples.BaseTask"))
                                // when instrumenting toString it is not desired to call super toString method as it overrides the instrumented result
                                .transform(TransformerUtils.forMethod("toString", new TaskInterceptor("Instrumented by Base"), false))
                                .type(named("cz.cuni.mff.d3s.distrace.examples.ExtendedTask"))
                                .transform(TransformerUtils.forMethod("toString", new TaskInterceptor("Instrumented by Extended"), false));
                    }
                });

    }
}
