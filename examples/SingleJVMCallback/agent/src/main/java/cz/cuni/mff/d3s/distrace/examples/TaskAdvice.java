package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;

public class TaskAdvice {

    public static class run {

        @Advice.OnMethodEnter
        public static void enter(@Advice.This Task task) {
            TraceContext tc = TraceContext.getFromObject(task);
            tc.openNestedSpan("Thread execution span");
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Task task) {
            TraceContext tc = TraceContext.getFromObject(task);
            System.out.println("Method run on thread task was called. Thread id: " + Thread.currentThread().getId() +
                    ", trace id: " + tc.getTraceId());
            tc.closeCurrentSpan(); // close the current span in run method
            tc.closeCurrentSpan(); // close the parent span encapsulating whole task submission as well
        }
    }

}
