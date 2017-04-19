package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.instrumentation.BaseTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * Transformer for Starter task
 */
public class StarterTaskTransformer extends BaseTransformer {
    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        return builder.visit(Advice.to(StarterTaskAdvice.run.class).on(named("run"))).
                visit(Advice.to(StarterTaskAdvice.start.class).on(named("start")));
    }
}
