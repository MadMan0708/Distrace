package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Interceptor;
import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * Interceptor of starter task
 */
public class StarterTaskInterceptor implements Interceptor {

    public void start(@This Thread thread){
        InstrumentUtils.injectTraceContextOn(thread);
        System.out.println("Method start on starter task was called. Thread id: " + thread.getId());
    }

    public void run(){
        System.out.println("Method run on starter task was called. Thread id: " + Thread.currentThread().getId() + ", trace id: "+ InstrumentUtils.getTraceContext().getTraceId());

    }
}
