package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Instrumentor;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseTransformer;
import cz.cuni.mff.d3s.distrace.instrumentation.MainAgentBuilder;
import cz.cuni.mff.d3s.distrace.instrumentation.TransformerUtils;
import hex.ModelBuilder;
import hex.ModelBuilderAdvices;
import hex.tree.SharedTree;
import hex.tree.gbm.GBM;
import hex.tree.gbm.GBMAdvices;
import hex.tree.gbm.GBMDriverAdvices;
import hex.tree.gbm.SharedTreeDriverAdvices;
import javassist.ClassPool;
import javassist.ClassPoolInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import water.H2O;
import water.Job;
import water.JobAdvices;

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
                        .type(isSubTypeOf(ModelBuilder.class))
                        .transform(new BaseTransformer() {
                            @Override
                            public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
                                // define the trace context field and the instrumentation points
                                return TransformerUtils.defineTraceContextField(builder).
                                        visit(Advice.to(ModelBuilderAdvices.trainModel.class).on(named("trainModel")));
                            }
                        })
                        .type(isSubTypeOf(Job.class))
                        .transform(new BaseTransformer() {
                          @Override
                          public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
                            // define the trace context field and the instrumentation points
                            return TransformerUtils.defineTraceContextField(builder).
                                    visit(Advice.to(JobAdvices.get.class).on(named("get")));
                          }
                        })
                        .type(ElementMatchers.<TypeDescription>named("hex.tree.gbm.GBM$GBMDriver"))
                        .transform(new BaseTransformer() {
                            @Override
                            public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
                                // define the trace context field and the instrumentation points
                                return TransformerUtils.defineTraceContextField(builder).
                                        visit(Advice.to(GBMDriverAdvices.buildNextKTrees.class).on(named("buildNextKTrees")))
                                        .visit(Advice.to(GBMDriverAdvices.initializeModelSpecifics.class).on(named("initializeModelSpecifics")));
                            }
                        })
                        .type(ElementMatchers.<TypeDescription>named("hex.tree.SharedTree$Driver"))
                        .transform(new BaseTransformer() {
                            @Override
                            public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
                                // define the trace context field and the instrumentation points
                                return TransformerUtils.defineTraceContextField(builder).
                                        visit(Advice.to(SharedTreeDriverAdvices.scoreAndBuildTrees.class).on(named("scoreAndBuildTrees")))
                                        .visit(Advice.to(SharedTreeDriverAdvices.computeImpl.class).on(named("computeImpl")));
                            }
                        })
                        .type(is(GBM.class))
                        .transform(new BaseTransformer() {
                            @Override
                            public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
                                // define the trace context field and the instrumentation points
                                return TransformerUtils.defineTraceContextField(builder).
                                        visit(Advice.to(GBMAdvices.trainModelImpl.class).on(named("trainModelImpl")));
                            }
                        });

            }
        });
    }

}
