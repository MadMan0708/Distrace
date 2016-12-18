package water;

import cz.cuni.mff.d3s.distrace.Interceptor;
import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.implementation.bind.annotation.This;

import static cz.cuni.mff.d3s.distrace.utils.InstrumentUtils.getTraceContext;

public class MRTaskInterceptor implements Interceptor {

    public void onCompletion(@This Object o) {
        if (o instanceof SumMRTask) {
            // close span
            MRTask task = (MRTask) o;
            System.out.println("OnCompletition ( local reducing) was called on node: " + H2O.getIpPortString() + " trace ID " +  getTraceContext(o).getTraceId());
        }
    }

    public void setupLocal0(@This Object o){
        if( o instanceof SumMRTask) {
            InstrumentUtils.getTraceContext(o).nestSpan();
            MRTask task = (MRTask) o;
            System.out.println("SetupLocal0 ( dist prepare) was called on node: " + H2O.getIpPortString() + " trace ID " + getTraceContext(o).getTraceId());
        }
    }

    public void doAll(@This Object o){
        // start main span
        if( o instanceof SumMRTask) {
            InstrumentUtils.setTraceId(o);
            System.out.println("doAll was called on node: " + H2O.getIpPortString() + " trace ID " + getTraceContext(o).getTraceId());
        }
    }
}
