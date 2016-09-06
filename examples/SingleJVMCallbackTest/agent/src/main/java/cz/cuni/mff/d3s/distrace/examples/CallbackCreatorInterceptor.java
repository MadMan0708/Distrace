package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.api.TraceContext;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by kuba on 05/09/16.
 */
public class CallbackCreatorInterceptor {
    @RuntimeType
    public static Object createCallback(@Origin Method method, @AllArguments Object[] arguments){

        // invoke the original method
        // final Object invoke = method.invoke(proxy, arguments);
        Object invoke = null;
        try {
           invoke = method.invoke(null, arguments); // we are instrumenting static method, thus we pass null here
            // set the trace context on this class
           invoke.getClass().getDeclaredField("traceContext").set(invoke, new TraceContext());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return invoke;
    }
}
