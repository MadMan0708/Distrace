package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Instrumentor;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import water.Compute2Advice;
import water.MRTaskInterceptor;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.TransformerUtils;
import javassist.ClassPool;
import javassist.ClassPoolInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import water.MRTask;
import water.RemoteComputeAdvice;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isSubTypeOf;

public class Starter {
    public static void main(String args[]){
        new Instrumentor().start(args, new CustomAgentBuilder() {
            @Override
            public AgentBuilder createAgent(BaseAgentBuilder builder, String pathToGeneratedClasses) {
                return builder
                        .type(is(ClassPool.class))
                        .transform(TransformerUtils.forMethodsIn(new ClassPoolInterceptor(pathToGeneratedClasses)))
                        .type(isSubTypeOf(MRTask.class))
                        .transform(new AgentBuilder.Transformer(){
                            @Override
                            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
                                return TransformerUtils.forMethodsInInterceptor(builder, new MRTaskInterceptor()).
                                        visit(Advice.to(Compute2Advice.class).on(ElementMatchers.named("compute2"))).
                                        visit(Advice.to(RemoteComputeAdvice.class).on(ElementMatchers.named("remote_compute")));
                            }
                        })
                        .transform(TransformerUtils.withTraceIdForMethodsIn(new MRTaskInterceptor()));
            }
        });
    }

}
