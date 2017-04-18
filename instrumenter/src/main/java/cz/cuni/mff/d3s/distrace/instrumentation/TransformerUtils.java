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

import static net.bytebuddy.matcher.ElementMatchers.named;


public class TransformerUtils {

    public static AgentBuilder.Transformer forMethod(String methodName, final Interceptor interceptor){
        return new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                return builder.method(named("toString")).intercept(MethodDelegation.to(interceptor));
            }
        };
    }
    public static AgentBuilder.Transformer withTraceIdForMethodsIn(final Interceptor interceptor){
        return new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                return TransformerUtils.forMethodsInInterceptor(TransformerUtils.defineTraceId(builder), interceptor);
            }

        };
    }
    public static AgentBuilder.Transformer forMethodsIn(final Interceptor interceptor){
        return new AgentBuilder.Transformer(){
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                return forMethodsInInterceptor(builder, interceptor);
            }
        };
    }

    public static DynamicType.Builder<?> forMethodsInInterceptor(DynamicType.Builder<?> builder, Interceptor interceptor, String[] ignore){
        Method[] declaredMethods = interceptor.getClass().getDeclaredMethods();

        String[] methodNames = new String[declaredMethods.length];
        for(int i = 0; i<declaredMethods.length; i++){
            // don't take static method into account - they are usually advice methods
            if(!Modifier.isStatic(declaredMethods[i].getModifiers()) && !Arrays.asList(ignore).contains(methodNames[i])) {
                methodNames[i] = declaredMethods[i].getName();
            }
        }
        return forMethods(builder, methodNames, interceptor);
    }
    public static DynamicType.Builder<?> forMethodsInInterceptor(DynamicType.Builder<?> builder, Interceptor interceptor){
        return forMethodsInInterceptor(builder, interceptor, new String[]{});
    }

    private static DynamicType.Builder<?> forMethods(DynamicType.Builder<?> builder, String[] methods, Interceptor interceptor){

        if(methods == null || methods.length == 0){
            return builder;
        }else{
            ElementMatcher.Junction<NamedElement> matcher = ElementMatchers.named(methods[0]);
            for(int i = 1; i< methods.length; i++){
                matcher = matcher.or(ElementMatchers.named(methods[i]));
            }
            return builder.method(matcher).intercept(MethodDelegation.to(interceptor).andThen(SuperMethodCall.INSTANCE));
        }

    }

    public static DynamicType.Builder<?> defineField(DynamicType.Builder<?> builder, Class clazz, String name){
        return builder.defineField(name, clazz, Visibility.PRIVATE);
    }

    public static DynamicType.Builder<?> defineTraceId(DynamicType.Builder<?> builder){
        return defineField(builder, TraceContext.class, "____traceId");
    }

    /**
     Field traceContextField = that.getClass().getDeclaredField("traceContext");
     traceContextField.setAccessible(true);
     ThreadLocal<TraceContext> tLocal = new ThreadLocal<TraceContext>();
     tLocal.set(new TraceContext());
     traceContextField.set(that, tLocal);
     //noinspection unchecked
     ThreadLocal<TraceContext> tLocalContext = (ThreadLocal<TraceContext>)traceContextField.getOrCreateFrom(that);
     TraceContext traceContext = tLocalContext.getOrCreateFrom();

     */
}
