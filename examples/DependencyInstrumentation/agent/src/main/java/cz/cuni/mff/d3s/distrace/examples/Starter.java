package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.CustomAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.TransformerUtils;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.utility.JavaModule;

import static net.bytebuddy.matcher.ElementMatchers.named;


/**
 * Starter of the instrumentation server. This example does not use the user interface however it just demonstrates
 * the instrumentation of dependent classes. Interceptor API is used for instrumentation in this example.
 */
public class Starter {
    public static void main(String[] args) {
        new Instrumentor().start(args,
                new CustomAgentBuilder() {
                    @Override
                    public AgentBuilder createAgent(BaseAgentBuilder builder) {
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
