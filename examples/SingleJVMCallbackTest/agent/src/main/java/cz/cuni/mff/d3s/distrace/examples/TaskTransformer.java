package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.transformers.BaseTransformer;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Created by kuba on 16/09/16.
 */
public class TaskTransformer extends BaseTransformer {
    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("start")).intercept(MethodDelegation.to(TaskInterceptor.class)
                .andThen(SuperMethodCall.INSTANCE)).method(ElementMatchers.named("run")).intercept(MethodDelegation.to(TaskInterceptor.class)
                .andThen(SuperMethodCall.INSTANCE));
    }
}
