package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.transformers.BaseTransformer;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Created by kuba on 06/09/16.
 */
public class CallBackCreatorTransformer extends BaseTransformer {
    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        return builder.method(ElementMatchers.named("createCallback"))
                .intercept(MethodDelegation.to(CallbackCreatorInterceptor.class));
    }
}
