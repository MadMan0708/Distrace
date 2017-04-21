package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import cz.cuni.mff.d3s.distrace.utils.ReflectionUtils;
import net.bytebuddy.asm.Advice;

public class ExecutorAdvice {
    public static class submitTask {
        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0) Runnable task) {
            TraceContext tc = TraceContext.getFromObject(ReflectionUtils.getFieldValue(task, "callback"));
            tc.deepCopy().attachOnObject(task);
            tc.openNestedSpan("Task submission span");
            System.out.println("Trace context attached to the Task!");
        }
    }
}
