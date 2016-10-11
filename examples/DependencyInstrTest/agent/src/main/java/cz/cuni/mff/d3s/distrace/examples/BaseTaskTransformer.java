package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.transformers.BaseTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;

public class BaseTaskTransformer extends BaseTransformer{
    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        return builder.visit(Advice.to(BaseTaskAdvice.class).on(ElementMatchers.named("toString")));
    }
}
