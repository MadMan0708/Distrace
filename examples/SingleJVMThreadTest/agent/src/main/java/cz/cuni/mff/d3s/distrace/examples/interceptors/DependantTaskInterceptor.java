package cz.cuni.mff.d3s.distrace.examples.interceptors;

import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * Interceptor of dependant task
 */
public class DependantTaskInterceptor {

    public static void start(@This Thread thread){
        InstrumentUtils.propagateTraceContext(thread);
        System.out.println("Method start on dependant task was called. Thread id: " + thread.getId());
    }

    public static void run(){
        System.out.println("Method run on dependant task was called. Thread id: " + Thread.currentThread().getId() +
                ", trace id: "+ InstrumentUtils.getTraceContext().getTraceId());

    }
}
