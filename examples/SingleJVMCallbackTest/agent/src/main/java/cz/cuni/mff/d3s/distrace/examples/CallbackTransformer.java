package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import cz.cuni.mff.d3s.distrace.instrumentation.BaseTransformer;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;

public class CallbackTransformer extends BaseTransformer {
    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        // just define the field. The field is null at this moment
        return builder.defineField("traceContext", TraceContext.class, Visibility.PRIVATE);
    }
}
