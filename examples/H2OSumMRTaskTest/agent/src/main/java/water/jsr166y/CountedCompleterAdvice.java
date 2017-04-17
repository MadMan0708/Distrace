package water.jsr166y;

import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.instrumentation.InstrumentUtils;
import cz.cuni.mff.d3s.distrace.instrumentation.StackTraceUtils;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import jsr166y.CountedCompleter;
import net.bytebuddy.asm.Advice;

public class CountedCompleterAdvice {

    public static class __tryComplete {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This CountedCompleter completer, @Advice.Argument(0) Object arg) {

            if (arg instanceof SumMRTask) {
                TraceContext tc = TraceContext.getWithoutAttachFrom(arg);
                if (InstrumentUtils.storage3.contains(arg) && ((SumMRTask) arg).getPendingCount() == 0) {
                    tc.getCurrentSpan().appendToName(" __try_complete called" + completer.getPendingCount());
                    tc.closeCurrentSpan();
                }


            }
        }
    }
}
