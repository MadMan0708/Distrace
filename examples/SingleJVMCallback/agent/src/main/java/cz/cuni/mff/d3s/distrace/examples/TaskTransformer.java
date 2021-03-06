package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.instrumentation.BaseTransformer;
import cz.cuni.mff.d3s.distrace.instrumentation.TransformerUtils;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;

public class TaskTransformer extends BaseTransformer {
    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        return TransformerUtils.defineTraceContextField(builder)
                .visit(Advice.to(TaskAdvice.run.class).on(ElementMatchers.named("run")));
    }
}
