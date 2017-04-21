package cz.cuni.mff.d3s.distrace.examples;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;


public class CallbackCreatorAdvice {

    @Advice.OnMethodExit
    public static Object exit(@Advice.Return Callback value) {
        TraceContext tc = TraceContext.create().attachOnObject(value);
        tc.openNestedSpan("Main Callback Span");
        System.out.println("Created callback with trace ID = " + tc.getTraceId());
        return value;
    }
}
