package water.jsr166y;


import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.instrumentation.InstrumentUtils;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;
import water.MRTask;

public class ForkJoinPoolAdvice {

    public static class poll {
        @Advice.OnMethodExit
        public static void exit(@Advice.Return Object o) {
            System.out.println("POLL ON FJQ CALLED");

            if (o instanceof SumMRTask) {
                MRTask task = (MRTask) o;
                TraceContext.getOrCreateFrom(task).getCurrentSpan().add("Polled from FJQ", System.nanoTime() / 1000);
                InstrumentUtils.getCurrentSpan().save(); // save without going one level up
            }
        }
    }
}
