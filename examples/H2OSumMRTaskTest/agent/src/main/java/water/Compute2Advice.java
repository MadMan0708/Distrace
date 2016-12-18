package water;

import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.asm.Advice;

import static cz.cuni.mff.d3s.distrace.utils.InstrumentUtils.getTraceContext;

/**
 * Created by kuba on 18/12/2016.
 */
public class Compute2Advice {

    @Advice.OnMethodExit
    public static void compute2(@Advice.This Object o) {
        if (o instanceof SumMRTask) {
            // start span
            InstrumentUtils.getCurrentSpan(o).setName("MRTask local work").store();
            MRTask task = (MRTask) o;
            System.out.println("Compute2 (Local work) was called on node: " + H2O.getIpPortString() + " trace ID " + getTraceContext(o).getTraceId());
        }
    }
}
