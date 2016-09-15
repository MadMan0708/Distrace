package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.api.TraceContext;
import net.bytebuddy.asm.Advice.BoxedReturn;
import net.bytebuddy.asm.Advice.OnMethodExit;

import java.lang.reflect.Field;

/**
 * Created by kuba on 15/09/16.
 */
public class MyAdvice {

    @OnMethodExit
    public static Object exit(@BoxedReturn Object value) {
        System.out.println("CALLED");
        try {
            Field f = value.getClass().getDeclaredField("traceContext");
            f.setAccessible(true);
            f.set(value, new TraceContext());
            TraceContext context = (TraceContext) f.get(value);
            System.out.println("Trace ID = " +context.getTraceId());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return value;
    }
}
