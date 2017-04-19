package cz.cuni.mff.d3s.distrace.instrumentation;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Several helper methods for creating transformers in a more concise way
 */
public class TransformerUtils {
    private static final String traceContextFieldName = "____traceContext";

    /**
     * Create a transformation builder for the methods with the same names as the methods defined in the interceptor
     *
     * @param interceptor interceptor
     * @param callSuper   call the original method after the instrumented one
     * @return created builder
     */
    public static AgentBuilder.Transformer forInterceptorMethods(final Interceptor interceptor, final boolean callSuper) {
        return new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                return forInterceptorMethods(builder, interceptor, callSuper);
            }
        };
    }

    /**
     * Create a transformation builder for the methods with the same names as the methods defined in the interceptor
     * and skip the ignored methods
     *
     * @param interceptor interceptor
     * @param ignore      method names to ignore
     * @param callSuper   call the original method after the instrumented one
     * @return created builder
     */
    public static AgentBuilder.Transformer forInterceptorMethods(final Interceptor interceptor, final String[] ignore, final boolean callSuper) {
        return new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                return forInterceptorMethods(builder, interceptor, ignore, callSuper);
            }
        };
    }

    /**
     * Create a transformation builder for the specified method name and the interceptor
     *
     * @param methodName  method name
     * @param interceptor interceptor
     * @param callSuper   call the original method after the instrumented one
     * @return created builder
     */
    public static AgentBuilder.Transformer forMethod(final String methodName, final Interceptor interceptor, boolean callSuper) {
        return forMethods(new String[]{methodName}, interceptor, callSuper);
    }

    /**
     * Create a transformation builder for the specified names and the interceptor
     *
     * @param methods     method names
     * @param interceptor interceptor
     * @param callSuper   call the original method after the instrumented one
     * @return created builder
     */
    public static AgentBuilder.Transformer forMethods(final String[] methods, final Interceptor interceptor, final boolean callSuper) {
        return new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                return forMethods(builder, methods, interceptor, callSuper);
            }
        };
    }

    /**
     * Create a transformation builder from existing builder which performs instrumentation on methods with the same names
     * as the methods defined in the interceptor class.
     *
     * @param builder     original builder
     * @param interceptor interceptor
     * @param callSuper   call the original method after the instrumented one
     * @return altered builder
     */
    public static DynamicType.Builder<?> forInterceptorMethods(DynamicType.Builder<?> builder, Interceptor interceptor, boolean callSuper) {
        return forInterceptorMethods(builder, interceptor, new String[]{}, callSuper);
    }


    /**
     * Create a transformation builder from existing builder which performs instrumentation on methods with the same names
     * as the methods defined in the interceptor class and skip the ignore methods.
     *
     * @param builder     original builder
     * @param interceptor interceptor
     * @param ignore      list of methods to ignore
     * @param callSuper   call the original method after the instrumented one
     * @return altered builder
     */
    public static DynamicType.Builder<?> forInterceptorMethods(DynamicType.Builder<?> builder, Interceptor interceptor, String[] ignore, boolean callSuper) {
        Method[] declaredMethods = interceptor.getClass().getDeclaredMethods();

        String[] methodNames = new String[declaredMethods.length];
        for (int i = 0; i < declaredMethods.length; i++) {
            // don't take static method into account - they are usually advice methods
            if (!Modifier.isStatic(declaredMethods[i].getModifiers()) && !Arrays.asList(ignore).contains(methodNames[i])) {
                methodNames[i] = declaredMethods[i].getName();
            }
        }
        return forMethods(builder, methodNames, interceptor, callSuper);
    }

    /**
     * Create a transformation builder from existing builder which performs instrumentation on provided method name using the provided
     * interceptor
     *
     * @param builder     original builder
     * @param methodName  method name to instrument
     * @param interceptor interceptor
     * @param callSuper   call the original method after the instrumented one
     * @return altered builder
     */
    public static DynamicType.Builder<?> forMethod(DynamicType.Builder<?> builder, String methodName, Interceptor interceptor, boolean callSuper) {
        return forMethods(builder, new String[]{methodName}, interceptor, callSuper);
    }

    /**
     * Create a transformation builder from existing builder which performs instrumentation on provided methods using the provided
     * interceptor
     *
     * @param builder     original builder
     * @param methods     method names to instrument
     * @param interceptor interceptor
     * @param callSuper   call the original method after the instrumented one
     * @return altered builder
     */
    public static DynamicType.Builder<?> forMethods(DynamicType.Builder<?> builder, String[] methods, Interceptor interceptor, boolean callSuper) {
        if (methods == null || methods.length == 0) {
            return builder;
        } else {
            ElementMatcher.Junction<NamedElement> matcher = ElementMatchers.named(methods[0]);
            for (int i = 1; i < methods.length; i++) {
                matcher = matcher.or(ElementMatchers.named(methods[i]));
            }
            if (callSuper) {
                return builder.method(matcher).intercept(MethodDelegation.to(interceptor).andThen(SuperMethodCall.INSTANCE));
            } else {
                return builder.method(matcher).intercept(MethodDelegation.to(interceptor));
            }
        }
    }


    /**
     * Create a trace context field on the specified builder
     *
     * @param builder original builder
     * @return altered builder
     */
    public static DynamicType.Builder<?> defineTraceContextField(DynamicType.Builder<?> builder) {
        return defineField(builder, TraceContext.class, traceContextFieldName);
    }

    /**
     * Create a field on the builder object
     *
     * @param builder original builder
     * @param clazz   type of the field
     * @param name    name of the field
     * @return altered builder
     */
    private static DynamicType.Builder<?> defineField(DynamicType.Builder<?> builder, Class clazz, String name) {
        return builder.defineField(name, clazz, Visibility.PRIVATE);
    }
}
