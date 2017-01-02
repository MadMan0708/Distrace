package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.instrumentation.BaseTransformer;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * Transformer for DependantTask
 */
public class DependantTaskTransformer extends BaseTransformer {
    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        return builder.method(named("start").or(named("run"))).intercept(MethodDelegation.to(new DependantTaskInterceptor())
                .andThen(SuperMethodCall.INSTANCE));
    }
}
