package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.ReflectionUtils;
import cz.cuni.mff.d3s.distrace.utils.TransformerUtils;
import javassist.ClassPool;
import javassist.ClassPoolInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import water.*;
import water.fvec.Frame;

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
                                    Method doAll = ReflectionUtils.findMethod(MRTask.class, "doAll", Frame.class);

                                    return  TransformerUtils.defineTraceId(builder).
                                            visit(Advice.to(MRTaskAdvices.setupLocal0.class).on(named("setupLocal0"))).
                                            visit(Advice.to(MRTaskAdvices.remote_compute.class).on(named("remote_compute"))).
                                            visit(Advice.to(MRTaskAdvices.compute2.class).on(named("compute2"))).
                                            visit(Advice.to(MRTaskAdvices.doAll.class).on(ElementMatchers.is(doAll)));
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
                        });
            }
        });
    }

}
