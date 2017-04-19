package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;

/**
 * Around advice for the starter task
 */
public class StarterTaskAdvice {

    public static class start {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Thread thizz) {
            TraceContext.create().attachOnTread(thizz).openNestedSpan("Starter Span");
            System.out.printf("Method start on starter task was called. Thread id: %d\n", thizz.getId());
        }
    }

    public static class run {
        @Advice.OnMethodExit
        public static void exit() {
            TraceContext tc = TraceContext.getFromCurrentThread();
            System.out.printf("Method run on starter task was called. Thread id = %d, trace id = %s, span id = %s\n",
                    Thread.currentThread().getId(), tc.getTraceId(), tc.getCurrentSpan().getSpanId());
            tc.closeCurrentSpan();
        }
    }
}
