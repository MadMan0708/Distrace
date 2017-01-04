package water;

import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.tracing.Span;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;


public class H2OAdvices {

    public static class submitTask {
        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0) Object o) {
            H2O.H2OCountedCompleter dt = (H2O.H2OCountedCompleter) o;
            if(dt instanceof SumMRTask) {
                MRTask tsk = (SumMRTask)dt;
                Span span = TraceContext.getOrCreateFrom(dt).getCurrentSpan();
                if (span.hasAnnotation("RPC Called")) {
                    span.add("RPC Submitted", System.nanoTime() / 1000)
                            .add("Frame key", tsk._fr + "")
                            .add("Transmission time", span.getLongValue("RPC Submitted") - span.getLongValue("RPC Called"));
                }
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.Argument(0) Object o) {
            H2O.H2OCountedCompleter dt = (H2O.H2OCountedCompleter) o;
            if(dt instanceof SumMRTask){
                // save current span without going one level up
                TraceContext.getOrCreateFrom(o).getCurrentSpan().save();
            }
        }
    }

}
