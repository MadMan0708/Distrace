package water;

import cz.cuni.mff.d3s.distrace.Interceptor;
import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import static cz.cuni.mff.d3s.distrace.utils.InstrumentUtils.getTraceContext;

public class MRTaskInterceptor implements Interceptor {


/*
    public void onCompletion(@This Object o) {
        if (o instanceof SumMRTask) {
            // close span
            InstrumentUtils.getTraceContext(o).storeCurrentSpan();
            MRTask task = (MRTask) o;
            System.out.println("OnCompletition ( local reducing) was called on node: " + H2O.getIpPortString() + " trace ID " +  getTraceContext(o).getTraceIdFrom());
        }
    }*/


    public void setupLocal0(@This Object o){
        //supr.run();

    }
}
