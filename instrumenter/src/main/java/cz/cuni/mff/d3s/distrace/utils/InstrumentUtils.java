package cz.cuni.mff.d3s.distrace.utils;

import cz.cuni.mff.d3s.distrace.TraceContextManager;
import cz.cuni.mff.d3s.distrace.api.Span;
import cz.cuni.mff.d3s.distrace.api.TraceContext;

import java.lang.reflect.Field;


public class InstrumentUtils {
    private static final String traceIdFieldName = "____traceId";
    public static TraceContext getTraceContext(Object thizz){
        try {
            Field f = thizz.getClass().getDeclaredField(traceIdFieldName);
            f.setAccessible(true);
            return (TraceContext)f.get(thizz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Span getCurrentSpan(Object o){
        try {
            Field f = o.getClass().getDeclaredField(traceIdFieldName);
            f.setAccessible(true);
            return ((TraceContext)f.get(o)).getCurrentSpan();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setTraceId(Object thizz){
        try {
            Field f = thizz.getClass().getDeclaredField(traceIdFieldName);
            f.setAccessible(true);
            if(f.get(thizz) == null) {
                f.set(thizz, new TraceContext());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Span getCurrentSpan(){
       return getTraceContext().getCurrentSpan();
    }

    public static TraceContext injectTraceContext(){
        return injectTraceContextOn(Thread.currentThread());
    }

    public static TraceContext injectTraceContextOn(Thread thread){
        return TraceContextManager.getOrCreate(thread, new TraceContext());
    }

    public static void propagateTraceContext(Thread to){
        TraceContext context = TraceContextManager.get(Thread.currentThread());
        TraceContextManager.getOrCreate(to, context.nestSpan());
    }

    public static TraceContext getTraceContext(){
        return TraceContextManager.get(Thread.currentThread());
    }
}
