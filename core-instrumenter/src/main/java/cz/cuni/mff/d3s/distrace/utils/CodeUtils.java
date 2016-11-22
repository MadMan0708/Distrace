package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;

/**
 * Helper methods for code generation
 */
public class CodeUtils {

    public static DynamicType.Builder<?> defineField(DynamicType.Builder<?> builder, Class clazz, String name){
        return builder.defineField(name, clazz, Visibility.PRIVATE);
    }

    public static DynamicType.Builder<?> defineTraceId(DynamicType.Builder<?> builder){
        return defineField(builder, Long.class, "____traceId");
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