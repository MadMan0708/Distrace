package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;

public class CallbackAdvice {

    public static class call {
        @Advice.OnMethodExit
        public static void exit(@Advice.This Callback callback) {
            TraceContext tc = TraceContext.getFromObject(callback);
            tc.closeCurrentSpan();
        }
    }
}
