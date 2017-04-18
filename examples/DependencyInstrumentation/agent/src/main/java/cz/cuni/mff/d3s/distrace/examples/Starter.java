package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.CustomAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.TransformerUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;

import static net.bytebuddy.matcher.ElementMatchers.named;


/**
 * Starter of the instrumentation server
 */
public class Starter {
    public static void main(String[] args) {
        new Instrumentor().start(args,
                new CustomAgentBuilder() {
                    @Override
                    public AgentBuilder createAgent(BaseAgentBuilder builder, String pathToGeneratedClasses) {
                        return builder
                                .type(named("cz.cuni.mff.d3s.distrace.examples.BaseTask"))
                                .transform(TransformerUtils.forMethod("toString", new TaskInterceptor("Instrumented by Base")))
                                .type(named("cz.cuni.mff.d3s.distrace.examples.ExtendedTask"))
                                .transform(TransformerUtils.forMethod("toString", new TaskInterceptor("Instrumented by Extended")));
                    }
                });

    }
}
