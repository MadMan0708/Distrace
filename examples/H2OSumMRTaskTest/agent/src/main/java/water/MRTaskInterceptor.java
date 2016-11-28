package water;

import cz.cuni.mff.d3s.distrace.Interceptor;
import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.implementation.bind.annotation.This;

public class MRTaskInterceptor implements Interceptor {
    public void compute2(@This Object o) {
        if (o instanceof SumMRTask) {
            // start span
            MRTask task = (MRTask) o;
            System.out.println("Compute2 (Local work) was called on node: " + H2O.getIpPortString() + " trace ID " + InstrumentUtils.getTraceId(o));
        }
    }

    public void onCompletion(@This Object o) {
        if (o instanceof SumMRTask) {
            // close span
            MRTask task = (MRTask) o;
            System.out.println("OnCompletition ( local reducing) was called on node: " + H2O.getIpPortString() + " trace ID " + InstrumentUtils.getTraceId(o));
        }
    }

    public void setupLocal0(@This Object o){
        if( o instanceof SumMRTask) {
            MRTask task = (MRTask) o;
            System.out.println("SetupLocal0 ( dist prepare) was called on node: " + H2O.getIpPortString() + " trace ID " + InstrumentUtils.getTraceId(o));
        }
    }

    public void doAll(@This Object o){
        // start main span
        if( o instanceof SumMRTask) {
            InstrumentUtils.setTraceId(o);
            System.out.println("doAll was called on node: " + H2O.getIpPortString() + " trace ID " + InstrumentUtils.getTraceId(o));
        }
    }
}
