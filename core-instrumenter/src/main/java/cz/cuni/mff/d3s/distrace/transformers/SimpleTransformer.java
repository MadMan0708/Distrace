package cz.cuni.mff.d3s.distrace.transformers;

import cz.cuni.mff.d3s.distrace.interceptors.SimpleInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

public class SimpleTransformer implements AgentBuilder.Transformer {

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
        return builder.method(ElementMatchers.nameEndsWith("print"))
                .intercept(MethodDelegation.to(SimpleInterceptor.class).andThen(SuperMethodCall.INSTANCE));

    }
}
