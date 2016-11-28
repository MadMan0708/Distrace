package cz.cuni.mff.d3s.distrace.utils;

import cz.cuni.mff.d3s.distrace.TraceContextManager;
import cz.cuni.mff.d3s.distrace.api.TraceContext;

import java.lang.reflect.Field;

/**
 * Created by kuba on 20/11/2016.
 */
public class InstrumentUtils {

    public static long getTraceId(Object thizz){
        Field f = null;
        try {
            f = thizz.getClass().getDeclaredField("____traceId");
            f.setAccessible(true);
            return (Long)f.get(thizz);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void setTraceId(Object thizz){
        try {
            Field f = thizz.getClass().getDeclaredField("____traceId");
            f.setAccessible(true);
            if(f.get(thizz) == null) {
                f.set(thizz, new TraceContext().getTraceId());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static TraceContext injectTraceContext(){
        return injectTraceContextOn(Thread.currentThread());
    }

    public static TraceContext injectTraceContextOn(Thread thread){
        return TraceContextManager.getOrCreate(thread, new TraceContext());
    }

    public static void propagateTraceContext(Thread to){
        TraceContextManager.getOrCreate(to, TraceContextManager.get(Thread.currentThread()));
    }

    public static TraceContext getTraceContext(){
        return TraceContextManager.get(Thread.currentThread());
    }
}
