package cz.cuni.mff.d3s.distrace.utils;

import cz.cuni.mff.d3s.distrace.TraceContextManager;
import cz.cuni.mff.d3s.distrace.api.TraceContext;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;


/**
 * Helper methods for code generation
 */
public class CodeUtils {

    public static DynamicType.Builder<?> defineField(DynamicType.Builder<?> builder, Class clazz, String name){
        return builder.defineField(name, clazz, Visibility.PRIVATE);
    }

    public static void injectTraceContext(){
        injectTraceContextOn(Thread.currentThread());
    }

    public static void injectTraceContextOn(Thread thread){
        TraceContextManager.getOrCreate(thread, new TraceContext());
    }

    public static void propagateTraceContext(Thread to){
        TraceContextManager.getOrCreate(to, TraceContextManager.get(Thread.currentThread()));
    }

    public static TraceContext getTraceContext(){
        return TraceContextManager.get(Thread.currentThread());
    }

}

/**
 Field traceContextField = that.getClass().getDeclaredField("traceContext");
 traceContextField.setAccessible(true);
 ThreadLocal<TraceContext> tLocal = new ThreadLocal<TraceContext>();
 tLocal.set(new TraceContext());
 traceContextField.set(that, tLocal);
 //noinspection unchecked
 ThreadLocal<TraceContext> tLocalContext = (ThreadLocal<TraceContext>)traceContextField.get(that);
 TraceContext traceContext = tLocalContext.get();

 */