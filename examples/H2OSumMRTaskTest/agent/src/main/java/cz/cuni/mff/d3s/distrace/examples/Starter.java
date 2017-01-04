package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.CustomAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.ReflectionUtils;
import cz.cuni.mff.d3s.distrace.instrumentation.TransformerUtils;
import javassist.ClassPool;
import javassist.ClassPoolInterceptor;
import jsr166y.ForkJoinPool;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import water.*;
import water.fvec.Frame;
import water.jsr166y.ForkJoinPoolAdvice;

import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class Starter {
    public static void main(String args[]){
        new Instrumentor().start(args, new CustomAgentBuilder() {
            @Override
            public AgentBuilder createAgent(BaseAgentBuilder builder, String pathToGeneratedClasses) {
                return builder
                        .type(is(ClassPool.class))
                        .transform(TransformerUtils.forMethodsIn(new ClassPoolInterceptor(pathToGeneratedClasses)))
                        .type(isSubTypeOf(MRTask.class))
                        .transform(new AgentBuilder.Transformer() {
                            @Override
                            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
                                Method dfork = ReflectionUtils.findMethod(MRTask.class, "dfork", byte[].class, Frame.class, boolean.class);
                                Method dfork2 = ReflectionUtils.findMethod(MRTask.class, "dfork", Key[].class);

                                Method getResult = ReflectionUtils.findMethod(MRTask.class, "getResult", boolean.class);
                                return  TransformerUtils.defineTraceId(builder).
                                            visit(Advice.to(MRTaskAdvices.setupLocal0.class).on(named("setupLocal0"))).
                                            visit(Advice.to(MRTaskAdvices.remote_compute.class).on(named("remote_compute"))).
                                            visit(Advice.to(MRTaskAdvices.compute2.class).on(named("compute2"))).
                                           // visit(Advice.to(MRTaskAdvices.onCompletion.class).on(named("onCompletion"))).
                                            visit(Advice.to(MRTaskAdvices.dfork.class).on(anyOf(dfork, dfork2))).
                                            visit(Advice.to(MRTaskAdvices.getResult.class).on(is(getResult)));

                            }
                        })
                        .type(is(RPC.class))
                        .transform(new AgentBuilder.Transformer() {
                            @Override
                            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
                                Method call = ReflectionUtils.findMethod(RPC.class, "call");

                                return  builder.visit(Advice.to(RPCAdvices.call.class).on(is(call)));
                            }
                        })
                        .type(is(H2O.class))
                        .transform(new AgentBuilder.Transformer() {
                            @Override
                            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {

                                return  builder.visit(Advice.to(H2OAdvices.submitTask.class).on(named("submitTask")));
                            }
                        })
                        .type(is(ForkJoinPool.class))
                        .transform(new AgentBuilder.Transformer() {
                            @Override
                            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {

                                return  builder.visit(Advice.to(ForkJoinPoolAdvice.poll.class).on(named("poll")));
                            }
                        });
            }
        });
    }

}
