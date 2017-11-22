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
                        .transform(TransformerUtils.forInterceptorMethods(new ClassPoolInterceptor(pathToHelperClasses), true));

            }
        });
    }

}
