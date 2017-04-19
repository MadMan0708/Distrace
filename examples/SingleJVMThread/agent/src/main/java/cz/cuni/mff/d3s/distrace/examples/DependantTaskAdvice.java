package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;

/**
 * Around advice for the dependant task
 */
public class DependantTaskAdvice {

    public static class start {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Thread thizz) {
            System.out.println("THREAD NESTED" + thizz);
            TraceContext tc = TraceContext.getFromCurrentThread().deepCopy();
            tc.attachOnTread(thizz);
            tc.openNestedSpan("Nested Span");
            System.out.println("Method start on dependant task was called. Thread id: " + thizz.getId());
        }
    }

    public static class run {
        @Advice.OnMethodExit
        public static void exit(@Advice.This Thread thizz) {

            System.out.printf("Method run on dependant task was called. Thread id = %d, trace id = %s, span id = %s\n",
                    Thread.currentThread().getId(), TraceContext.getFromCurrentThread().getTraceId(), TraceContext.getFromCurrentThread().getCurrentSpan().getSpanId());
            TraceContext.getFromCurrentThread().closeCurrentSpan();
        }
    }
}
