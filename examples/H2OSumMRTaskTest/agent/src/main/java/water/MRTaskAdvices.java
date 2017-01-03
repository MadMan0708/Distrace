package water;

import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.instrumentation.InstrumentUtils;
import net.bytebuddy.asm.Advice;

import static cz.cuni.mff.d3s.distrace.instrumentation.InstrumentUtils.getTraceContext;
import static cz.cuni.mff.d3s.distrace.instrumentation.InstrumentUtils.getTraceContextFrom;


public abstract class MRTaskAdvices {

    public static class compute2 {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                // start span
                //InstrumentUtils.getOrCreateTraceContext(o)
                       // .openNestedSpan("MRTask local work")
                       // .add("ipPort", H2O.getIpPortString());
                //MRTask task = (MRTask) o;
                //System.out.println("Compute2 (Local work) was called on node: " + H2O.getIpPortString() + " trace ID "); // getTraceContext().getTraceId());
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                // close span
               // InstrumentUtils.storeAndCloseCurrentSpan();

                //InstrumentUtils.getTraceContext().storeAndCloseCurrentSpan();
                //MRTask task = (MRTask) o;
                //System.out.println("OnCompletition ( local reducing) was called on node: " + H2O.getIpPortString() + " trace ID "); // + getTraceContext().getTraceId());
            }
        }
    }

    public static class getResult {
        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                MRTask tsk = (MRTask)o;
                if(!((SumMRTask) o).isDone()) {
                    System.out.println("getResult: Storing Span with ID: " + InstrumentUtils.getCurrentSpan().getSpanId());
                    InstrumentUtils.storeAndCloseCurrentSpan();
                }
            }
        }
    }

    public static class dfork {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                InstrumentUtils.createTraceContext(o)
                        .openNestedSpan(H2O.getIpPortString() + " : MR Task Main Span")
                        .setIpPort(H2O.getIpPortString())
                        .add("ipPort", H2O.getIpPortString());

                System.out.println("doAll: Created Span with ID: " + InstrumentUtils.getCurrentSpan().getSpanId());
                System.out.println("doAll: Method was called on node: " + H2O.getIpPortString() + " trace ID " + getTraceContext().getTraceId() + " thread id " + Thread.currentThread().getId());
            }
        }
    }

    public static class remote_compute {

        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o){
            if (o instanceof SumMRTask) {
                // don't open nested span. Just start a new span
              InstrumentUtils.getOrCreateTraceContext(o)
                      .openNestedSpan(H2O.getIpPortString() + " : Remote Compute")
                      .setIpPort(H2O.getIpPortString())
                      .add("ipPort", H2O.getIpPortString());

                System.out.println("Remote compute: "+ getTraceContextFrom(o).getTraceId());
            }
        }
        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o, @Advice.Return RPC ret){
            if (o instanceof SumMRTask) {
                if(ret == null){
                    InstrumentUtils.getCurrentSpan().add("target", "local node");
                    System.out.println("No remote work");
                }else{
                    H2ONode node = ret._target;
                    InstrumentUtils.getCurrentSpan().add("target", ret._target.getIpPortString());
                    System.out.println("Computation planned on " + node.getIpPortString());
                    InstrumentUtils.getCurrentSpan().add("size of RPC", ret._dt.asBytes().length);
                }

                InstrumentUtils.getOrCreateTraceContext(o).storeAndCloseCurrentSpan();

                System.out.println("Remote compute was called on node: " + H2O.getIpPortString() + " trace ID " +  getTraceContext().getTraceId());
            }
        }
    }

    public static class setupLocal0 {

        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o){
            if( o instanceof SumMRTask) {
                InstrumentUtils.getOrCreateTraceContext(o)
                        .openNestedSpan(H2O.getIpPortString() + " : Local setup and splitting")
                        .setIpPort(H2O.getIpPortString());

            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o){
            if( o instanceof SumMRTask) {
                MRTask task = (MRTask) o;
                InstrumentUtils.getOrCreateTraceContext(o).getCurrentSpan()
                        .add("left", task._nleft == null ? "local": task._nleft._target.getIpPortString())
                        .add("right", task._nrite == null ? "local": task._nrite._target.getIpPortString());

                InstrumentUtils.getTraceContext().storeAndCloseCurrentSpan();
                System.out.println("SetupLocal0 ( dist prepare) was called on node: " + H2O.getIpPortString() + " trace ID " + getTraceContext().getTraceId());
            }
        }
    }

}
