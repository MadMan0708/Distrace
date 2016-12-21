package cz.cuni.mff.d3s.distrace.utils;

import com.sun.management.jmx.Trace;
import cz.cuni.mff.d3s.distrace.TraceContextManager;
import cz.cuni.mff.d3s.distrace.api.Span;
import cz.cuni.mff.d3s.distrace.api.TraceContext;

import java.lang.reflect.Field;


public class InstrumentUtils {
    private static final String traceIdFieldName = "____traceId";

    public static TraceContext getTraceContext(Object thizz) {
        try {
            Field f = thizz.getClass().getDeclaredField(traceIdFieldName);
            f.setAccessible(true);
            return (TraceContext) f.get(thizz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Span getCurrentSpan(Object o) {
        try {
            Field f = o.getClass().getDeclaredField(traceIdFieldName);
            f.setAccessible(true);
            return ((TraceContext) f.get(o)).getCurrentSpan();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setTraceIdOn(Object thizz, TraceContext context) {
        try {
            Field f = thizz.getClass().getDeclaredField(traceIdFieldName);
            f.setAccessible(true);
            f.set(thizz, context);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static TraceContext getTraceContextFrom(Object thizz) {
        try {
            Field f = thizz.getClass().getDeclaredField(traceIdFieldName);
            f.setAccessible(true);
            return (TraceContext)f.get(thizz);
        } catch (IllegalAccessException | NoSuchFieldException e1) {
            throw new RuntimeException("Trace ID field should be part of the class");
        }
    }


    public static TraceContext storeCurrentSpan() {
        return getTraceContext().storeCurrentSpan();
    }

    public static Span getCurrentSpan() {
        return getTraceContext().getCurrentSpan();
    }

    public static TraceContext injectTraceContext() {
        return injectTraceContextOn(Thread.currentThread());
    }

    public static TraceContext injectTraceContext(Object o) {
        injectTraceContextOn(Thread.currentThread());
        return getTraceContext();
    }

    public static TraceContext injectTraceContextOn(Thread thread) {
        return TraceContextManager.getOrCreate(thread, new TraceContext());
    }

    public static void propagateTraceContext(Thread to) {
        TraceContext context = TraceContextManager.get(Thread.currentThread());
        context.openNestedSpan();
        TraceContextManager.getOrCreate(to, context);
    }

    public static TraceContext getTraceContext() {
        return TraceContextManager.get(Thread.currentThread());
    }

    public static TraceContext getOrCreateTraceContext(Object o) {
        if(getTraceContextFrom(o) == null){
            if(TraceContextManager.get(Thread.currentThread()) == null) {
                TraceContextManager.getOrCreate(Thread.currentThread());
            }
            setTraceIdOn(o, TraceContextManager.get(Thread.currentThread()));
        } else {
            TraceContextManager.registerTraceContext(Thread.currentThread(), getTraceContextFrom(o));
        }
        return TraceContextManager.get(Thread.currentThread());
    }
}
