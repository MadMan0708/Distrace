package cz.cuni.mff.d3s.distrace.utils;

import com.sun.deploy.trace.Trace;
import cz.cuni.mff.d3s.distrace.TraceContextManager;
import cz.cuni.mff.d3s.distrace.api.TraceContext;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Field;


/**
 * Helper methods for code generation
 */
public class CodeUtils {

    public static DynamicType.Builder<?> defineField(DynamicType.Builder<?> builder, Class clazz, String name){
        builder.field(ElementMatchers.named("traceContext")).
        return builder.defineField(name, clazz, Visibility.PRIVATE);
    }

    public static void injectTraceContext(Object that){
        try {
            Thread thread = Thread.currentThread();
            TraceContext context = TraceContextManager.getOrCreate(thread, new TraceContext());

            Field traceContextField = that.getClass().getDeclaredField("traceContext");
            traceContextField.setAccessible(true);
            ThreadLocal<TraceContext> tLocal = new ThreadLocal<TraceContext>();
            tLocal.set(new TraceContext());
            traceContextField.set(that, tLocal);
            //noinspection unchecked
            ThreadLocal<TraceContext> tLocalContext = (ThreadLocal<TraceContext>)traceContextField.get(that);
            TraceContext traceContext = tLocalContext.get();

        } catch (NoSuchFieldException | IllegalAccessException e) {
           assert false;
        }
    }

}
