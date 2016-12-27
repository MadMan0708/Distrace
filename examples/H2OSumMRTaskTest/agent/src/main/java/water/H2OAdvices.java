package water;

import cz.cuni.mff.d3s.distrace.api.Span;
import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.asm.Advice;


public class H2OAdvices {

    public static class submitTask {
        @Advice.OnMethodEnter
        public static void enter(@Advice.Argument(0) Object o) {
            H2O.H2OCountedCompleter dt = (H2O.H2OCountedCompleter) o;
            if(dt instanceof SumMRTask) {
                Span s = InstrumentUtils.getOrCreateTraceContext(dt).getCurrentSpan();
                s.add("RPC Task Submitted", (System.nanoTime() / 1000) + "")
                        .add("Frame key", ((SumMRTask) dt)._fr + "");
                if (s.getLongValue("RPC Called") != null) {
                    s.add("Transmission time", s.getLongValue("RPC Task Submitted") - s.getLongValue("RPC Called"));
                }
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.Argument(0) Object o) {
            H2O.H2OCountedCompleter dt = (H2O.H2OCountedCompleter) o;
            if(dt instanceof SumMRTask){
                InstrumentUtils.getCurrentSpan().store(); // store without going one level up
            }
        }
    }

}
