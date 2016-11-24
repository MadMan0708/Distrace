package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.api.TraceContext;
import cz.cuni.mff.d3s.distrace.transformers.BaseTransformer;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class CallbackTransformer extends BaseTransformer {
    @Override
    public DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder) {
        // just define the field. The field is null at this moment
        return builder.defineField("traceContext", TraceContext.class, Visibility.PRIVATE);
    }
}
