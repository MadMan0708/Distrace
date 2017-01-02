package cz.cuni.mff.d3s.distrace.instrumentation;


import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;

public abstract class BaseTransformer implements AgentBuilder.Transformer {

   public abstract DynamicType.Builder<?> defineTransformation(DynamicType.Builder<?> builder);

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
        //DynamicType.Builder<?> initBuilder = CodeUtils.defineField(builder, ThreadLocal.class, "traceContext");

        // we keep this method in case we need to add additional information in the future
        return defineTransformation(builder);
    }
}
