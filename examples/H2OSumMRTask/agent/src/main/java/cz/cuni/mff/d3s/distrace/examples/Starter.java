package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseTransformer;
import cz.cuni.mff.d3s.distrace.instrumentation.MainAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.TransformerUtils;
import cz.cuni.mff.d3s.distrace.utils.ReflectionUtils;
import javassist.ClassPool;
import javassist.ClassPoolInterceptor;
import jsr166y.CountedCompleter;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import water.*;
import water.fvec.Frame;

import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Starter of the instrumentation server. This example demonstrates monitoring map reduce tasks in the H2O
 * distributed, in-memory machine learning platform.
 */
public class Starter {
    public static void main(String args[]) {
        new Instrumentor().start(args, new MainAgentBuilder() {
            @Override
            public AgentBuilder createAgent(BaseAgentBuilder builder, String pathToHelperClasses) {
                return builder
                        .type(is(ClassPool.class))
                        .transform(TransformerUtils.forInterceptorMethods(new ClassPoolInterceptor(pathToHelperClasses), true))
                        .type(isSubTypeOf(MRTask.class))
                        .transform(new BaseTransformer() {
                            @Override
                            public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
                                // get the methods
                                Method dfork = ReflectionUtils.getMethod(MRTask.class, "dfork", byte[].class, Frame.class, boolean.class);
                                Method dfork2 = ReflectionUtils.getMethod(MRTask.class, "dfork", Key[].class);
                                Method getResult = ReflectionUtils.getMethod(MRTask.class, "getResult", boolean.class);
                                Method remote_compute = ReflectionUtils.getMethod(MRTask.class, "remote_compute", int.class, int.class);
                                Method onCompletion = ReflectionUtils.getMethod(MRTask.class, "onCompletion", CountedCompleter.class);
                                Method reduce2 = ReflectionUtils.getMethod(MRTask.class, "reduce2", MRTask.class);

                                // define the trace context field and the instrumentation points
                                return TransformerUtils.defineTraceContextField(builder).
                                        visit(Advice.to(MRTaskAdvices.setupLocal0.class).on(named("setupLocal0"))).
                                        visit(Advice.to(MRTaskAdvices.remote_compute.class).on(is(remote_compute))).
                                        visit(Advice.to(MRTaskAdvices.compute2.class).on(named("compute2"))).
                                        visit(Advice.to(MRTaskAdvices.onCompletion.class).on(is(onCompletion))).
                                        visit(Advice.to(MRTaskAdvices.dfork.class).on(anyOf(dfork, dfork2))).
                                        visit(Advice.to(MRTaskAdvices.reduce2.class).on(is(reduce2))).
                                        visit(Advice.to(MRTaskAdvices.map.class).on(named("map"))).
                                        visit(Advice.to(MRTaskAdvices.getResult.class).on(is(getResult)));
                            }
                        })
                        .type(is(RPC.class))
                        .transform(new BaseTransformer() {
                            @Override
                            public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
                                Method call = ReflectionUtils.getMethod(RPC.class, "call");

                                return builder.visit(Advice.to(RPCAdvices.call.class).on(is(call))).
                                        visit(Advice.to(RPCAdvices.response.class).on(named("response")));
                            }
                        });

            }
        });
    }

}
