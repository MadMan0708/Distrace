package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.instrumentation.Interceptor;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * Interceptor of starter task
 */
public class StarterTaskInterceptor implements Interceptor {

    public void start(@This Thread thread){
        TraceContext tc = TraceContext.getCurrent();
        tc.openNestedSpan("Nested Span");
        tc.attachOnTread(thread);
        System.out.println("Method start on starter task was called. Thread id: " + thread.getId());
    }

    public void run(@SuperCall Runnable origin){
        origin.run();
        System.out.printf("Method run on starter task was called. Thread id = %d, trace id = %s, span id = %s\n",
                Thread.currentThread().getId(), TraceContext.getCurrent().getTraceId(), TraceContext.getCurrent().getCurrentSpan().getSpanId());
        TraceContext.getCurrent().getCurrentSpan().setName("Starter task").save();
    }
}
