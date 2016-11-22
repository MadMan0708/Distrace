package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Interceptor;
import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.implementation.bind.annotation.This;
import water.H2O;

public class SumMRTaskInterceptor implements Interceptor{

    transient int DEBUG_WEAVER = 1;

        public void map(@This Object o){
            SumMRTask task = (SumMRTask)o;
            System.out.println("Map was called on node: " + H2O.getIpPortString() +  " sum so far " + task.getSum() + " trace ID" + InstrumentUtils.getTraceId(o));
        }

        public void reduce(@This Object o){
            SumMRTask task = (SumMRTask)o;
            //System.out.println("Reduce was called on node: " + H2O.getIpPortString() +  " sum so far " + task.getSum() + " trace ID" + InstrumentUtils.getTraceId(o));
        }
}
