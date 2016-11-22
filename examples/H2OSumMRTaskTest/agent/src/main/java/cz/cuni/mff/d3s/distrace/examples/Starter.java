package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.CodeUtils;
import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import javassist.ClassPool;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import water.MRTask;
import water.fvec.Chunk;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class Starter {
    public static void main(String args[]){
        new Instrumentor().start(args, new CustomAgentBuilder() {
            @Override
            public AgentBuilder createAgent(BaseAgentBuilder builder) {
                return builder
                        //.type(is(SumMRTask.class))
                        //.transform(new SumMRTaskTransformer())
                        .type(isSubTypeOf(MRTask.class))
                        .transform(new MRTaskTransformer())
                        .type(is(ClassPool.class))
                        .transform(new ClassPoolTransformer());
            }
        });
    }

    private static class ClassPoolTransformer implements AgentBuilder.Transformer {

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
            return builder.visit(Advice.to(ClassPoolInterceptor.class).on(ElementMatchers.named("get")));
        }
    }
    private static class MRTaskTransformer implements AgentBuilder.Transformer {

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
            MRTaskInterceptor interceptor = new MRTaskInterceptor();
            return CodeUtils.defineTraceId(builder)
                    .method(named("compute2"))
                    .intercept(to(interceptor).andThen(SuperMethodCall.INSTANCE))
                    .method(named("setupLocal0"))
                    .intercept(to(interceptor).andThen(SuperMethodCall.INSTANCE))
                    .method(named("doAll"))
                    .intercept(to(interceptor).andThen(SuperMethodCall.INSTANCE));
        }
    }
    private static class SumMRTaskTransformer implements AgentBuilder.Transformer {

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
            return builder
                    .method(named("map").and(takesArguments(Chunk.class)))
                    .intercept(to(new SumMRTaskInterceptor()).andThen(SuperMethodCall.INSTANCE))
                    .method(named("reduce").and(takesArguments(SumMRTask.class)))
                    .intercept(to(new SumMRTaskInterceptor())
                            .andThen(SuperMethodCall.INSTANCE));
        }
    }
}
