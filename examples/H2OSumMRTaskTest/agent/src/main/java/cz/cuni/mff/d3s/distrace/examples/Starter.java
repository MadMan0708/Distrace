package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Instrumentor;
import water.MRTaskInterceptor;
import cz.cuni.mff.d3s.distrace.utils.BaseAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.CustomAgentBuilder;
import cz.cuni.mff.d3s.distrace.utils.TransformerUtils;
import javassist.ClassPool;
import javassist.ClassPoolInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import water.MRTask;

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
                        .transform(TransformerUtils.withTraceIdForMethodsIn(new MRTaskInterceptor()));
            }
        });
    }

}
