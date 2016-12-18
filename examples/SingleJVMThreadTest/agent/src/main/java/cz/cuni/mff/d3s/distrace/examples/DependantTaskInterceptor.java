package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.Interceptor;
import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * Interceptor of dependant task
 */
public class DependantTaskInterceptor implements Interceptor{

    public void start(@This Thread thread){
        InstrumentUtils.propagateTraceContext(thread);
        System.out.println("Method start on dependant task was called. Thread id: " + thread.getId());
    }

    public void run(){
        System.out.printf("Method run on dependant task was called. Thread id = %d, trace id = %d, span id = %d\n",
                Thread.currentThread().getId(), InstrumentUtils.getTraceContext().getTraceId(), InstrumentUtils.getCurrentSpan().getSpanId());
        InstrumentUtils.getCurrentSpan().setName("Dependant task").store();
    }
}
