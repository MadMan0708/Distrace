package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.implementation.bind.annotation.This;


public class TaskInterceptor {

    public void start(@This Thread thread){
        TraceContext tc = TraceContext.getCurrent();
        tc.openNestedSpan("Nested Span");
        tc.attachOnTread(thread);
        System.out.println("Method start on dependant task was called. Thread id: " + thread.getId());
    }

    public void run(){
        System.out.println("Method run on dependant task was called. Thread id: " + Thread.currentThread().getId() +
                ", trace id: "+ TraceContext.getCurrent().getTraceId());

    }
}