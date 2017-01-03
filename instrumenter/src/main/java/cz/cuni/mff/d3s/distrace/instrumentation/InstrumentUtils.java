package cz.cuni.mff.d3s.distrace.instrumentation;

import cz.cuni.mff.d3s.distrace.tracing.TraceContextManager;
import cz.cuni.mff.d3s.distrace.tracing.Span;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;

import java.lang.reflect.Field;


public class InstrumentUtils {

    private static final TraceContextManager contextManager = TraceContextManager.getOrCreate();
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
            throw new RuntimeException("No such field " + traceIdFieldName + " field should be part of the class " + thizz.getClass());
        }
    }


    public static void storeAndCloseCurrentSpan() {
        getTraceContext().storeAndCloseCurrentSpan();
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
        return contextManager.getOrCreateTraceContext(thread);
    }

    public static void propagateTraceContext(Thread to) {
        TraceContext context = contextManager.getTraceContext(Thread.currentThread());
        context.openNestedSpan();
        contextManager.attachTraceContextTo(to, context);
    }

    public static TraceContext getTraceContext() {
        return contextManager.getTraceContext(Thread.currentThread());
    }

    public static TraceContext createTraceContext(Object o) {
        contextManager.attachTraceContextTo(Thread.currentThread(), new TraceContext());
        setTraceIdOn(o, contextManager.getTraceContext(Thread.currentThread()));
        return contextManager.getTraceContext(Thread.currentThread());
    }

    public static TraceContext getOrCreateTraceContext(Object o) {
        if(getTraceContextFrom(o) == null){
            if(contextManager.getTraceContext(Thread.currentThread()) == null) {
                contextManager.getOrCreateTraceContext(Thread.currentThread());
            }
            setTraceIdOn(o, contextManager.getTraceContext(Thread.currentThread()));
        } else {
            contextManager.attachTraceContextTo(Thread.currentThread(), getTraceContextFrom(o));
        }
        return contextManager.getTraceContext(Thread.currentThread());
    }
}
