package cz.cuni.mff.d3s.distrace.instrumentation;

import cz.cuni.mff.d3s.distrace.tracing.TraceContextManager;
import cz.cuni.mff.d3s.distrace.tracing.Span;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;


public class InstrumentUtils {
    public static HashSet<Object> storage = new HashSet<>();
    public static HashSet<Object> storage2 = new HashSet<>();
    public static HashSet<Object> storage3 = new HashSet<>();


    public static final TraceContextManager contextManager = TraceContextManager.getOrCreate();
    public static final String traceContextFieldName = "____traceId";

    public static TraceContext getTraceContext(Object thizz) {
        try {
            Field f = thizz.getClass().getDeclaredField(traceContextFieldName);
            f.setAccessible(true);
            return (TraceContext) f.get(thizz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Span getCurrentSpan(Object o) {
        try {
            Field f = o.getClass().getDeclaredField(traceContextFieldName);
            f.setAccessible(true);
            return ((TraceContext) f.get(o)).getCurrentSpan();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void attachTraceContextOn(Object thizz, TraceContext context) {
        try {
            Field f = thizz.getClass().getDeclaredField(traceContextFieldName);
            f.setAccessible(true);
            f.set(thizz, context);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Span getCurrentSpan() {
        return TraceContext.getCurrent().getCurrentSpan();
    }

    public static TraceContext injectTraceContext() {
        return injectTraceContextOn(Thread.currentThread());
    }

    public static TraceContext injectTraceContext(Object o) {
        injectTraceContextOn(Thread.currentThread());
        return TraceContext.getCurrent();
    }

    public static TraceContext injectTraceContextOn(Thread thread) {
        return contextManager.getOrCreateTraceContext(thread);
    }

    public static void propagateTraceContext(Thread to) {
        TraceContext context = contextManager.getTraceContext(Thread.currentThread());
        context.openNestedSpan();
        contextManager.attachTraceContextTo(to, context);
    }


}
