package cz.cuni.mff.d3s.distrace.examples;


import cz.cuni.mff.d3s.distrace.Interceptor;
import net.bytebuddy.implementation.bind.annotation.This;
import water.H2O;

public class MRTaskInterceptor implements Interceptor{

        public void map(@This Object o){
            SumMRTask task = (SumMRTask)o;
            System.out.println("Map was called on node: " + H2O.getIpPortString() +  " sum so far " + task.getSum());
        }

        public void reduce(@This Object o){
            SumMRTask task = (SumMRTask)o;
            System.out.println("Reduce was called on node: " + H2O.getIpPortString() +  " sum so far " + task.getSum());
        }
}
