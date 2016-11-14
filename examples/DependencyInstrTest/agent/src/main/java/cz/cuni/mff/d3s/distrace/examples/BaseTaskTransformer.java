package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.transformers.BaseTransformer;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class BaseTaskTransformer extends BaseTransformer{

    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("toString")).intercept(MethodDelegation.to(new TaskInterceptor("Instrumented by Base")));
    }
}
