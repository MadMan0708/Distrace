package cz.cuni.mff.d3s.distrace.examples.transformers;

import cz.cuni.mff.d3s.distrace.examples.interceptors.StarterTaskInterceptor;
import cz.cuni.mff.d3s.distrace.transformers.BaseTransformer;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Transformer for Starter task
 */
public class StarterTaskTransformer extends BaseTransformer{
    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("start")).intercept(MethodDelegation.to(StarterTaskInterceptor.class)
                .andThen(SuperMethodCall.INSTANCE)).method(ElementMatchers.named("run")).intercept(MethodDelegation.to(StarterTaskInterceptor.class)
                .andThen(SuperMethodCall.INSTANCE));
    }
}
