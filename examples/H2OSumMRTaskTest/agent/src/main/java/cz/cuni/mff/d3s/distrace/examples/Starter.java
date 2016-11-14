package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import water.fvec.Chunk;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

public class Starter {
    public static void main(String args[]){
        new Instrumentor().start(args, new CustomAgentBuilder() {
            @Override
            public AgentBuilder createAgent(BaseAgentBuilder builder) {
                return builder.type(ElementMatchers.named(SumMRTask.class.getName())).transform(new
                MRTaskTransformer());
            }
        });
    }

    private static class MRTaskTransformer implements AgentBuilder.Transformer {

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
            return builder
                    .method(named("map").and(takesArguments(Chunk.class)))
                    .intercept(MethodDelegation.to(new MRTaskInterceptor())
                            .andThen(SuperMethodCall.INSTANCE))
                    .method(named("reduce").and(takesArguments(SumMRTask.class)))
                    .intercept(MethodDelegation.to(new MRTaskInterceptor())
                            .andThen(SuperMethodCall.INSTANCE));
        }
    }
}
