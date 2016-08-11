package cz.cuni.mff.d3s.distrace.examples.transformers;


import cz.cuni.mff.d3s.distrace.examples.interceptors.SimpleInterceptor;
import cz.cuni.mff.d3s.distrace.interceptors.FieldGetterSetter;
import cz.cuni.mff.d3s.distrace.utils.CodeUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.matcher.ElementMatchers;

public class SimpleTransformer implements AgentBuilder.Transformer {

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
       return CodeUtils.defineField(builder, String.class, "traceContext").method(ElementMatchers.nameEndsWith("print"))
                        .intercept(MethodDelegation.to(SimpleInterceptor.class)
                        .appendParameterBinder(FieldProxy.Binder.install(FieldGetterSetter.class))
                        .andThen(SuperMethodCall.INSTANCE));
    }
}
