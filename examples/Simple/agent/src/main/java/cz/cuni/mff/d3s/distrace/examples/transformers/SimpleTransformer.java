package cz.cuni.mff.d3s.distrace.examples.transformers;

import cz.cuni.mff.d3s.distrace.examples.interceptors.SimpleInterceptor;
import cz.cuni.mff.d3s.distrace.transformers.BaseTransformer;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;


public class SimpleTransformer extends BaseTransformer {
    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("print")).intercept(MethodDelegation.to(SimpleInterceptor.class)
                .andThen(SuperMethodCall.INSTANCE));
    }
}
