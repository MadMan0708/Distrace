package water;

import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.instrumentation.InstrumentUtils;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;

import static cz.cuni.mff.d3s.distrace.tracing.TraceContext.getCurrent;
import static cz.cuni.mff.d3s.distrace.tracing.TraceContext.getFrom;


public abstract class MRTaskAdvices {

    public static class dfork {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                TraceContext.createAndAttachTo(o)
                        .openNestedSpan(H2O.getIpPortString() + " : MR Task Main Span")
                        .setIpPort(H2O.getIpPortString())
                        .add("ipPort", H2O.getIpPortString());

                System.out.println("doAll: Created Span with ID: " + InstrumentUtils.getCurrentSpan().getSpanId());
                System.out.println("doAll: Method was called on node: " + H2O.getIpPortString() + " trace ID " + getCurrent().getTraceId() + " thread id " + Thread.currentThread().getId());
            }
        }
    }

    public static class getResult {
        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                System.out.println("getResult: Storing Span with ID: " + TraceContext.getOrCreateFrom(o).getCurrentSpan().getSpanId());
                TraceContext.getOrCreateFrom(o).closeCurrentSpan();
            }
        }
    }

    public static class setupLocal0 {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                TraceContext.getOrCreateFrom(o)
                        .openNestedSpan(H2O.getIpPortString() + " : Local setup and splitting")
                        .setIpPort(H2O.getIpPortString());
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                MRTask task = (MRTask) o;
                TraceContext.getOrCreateFrom(o).getCurrentSpan()
                        .add("left", task._nleft == null ? "local" : task._nleft._target.getIpPortString())
                        .add("right", task._nrite == null ? "local" : task._nrite._target.getIpPortString());

                getCurrent().closeCurrentSpan();
                System.out.println("SetupLocal0 ( dist prepare) was called on node: " + H2O.getIpPortString() + " trace ID " + getCurrent().getTraceId());
            }
        }
    }

    public static class remote_compute {

        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                TraceContext.getOrCreateFrom(o)
                        .openNestedSpan(H2O.getIpPortString() + " : Remote Compute Init -> Submit Task")
                        .setIpPort(H2O.getIpPortString())
                        .add("ipPort", H2O.getIpPortString());

                System.out.println("Remote compute enter: " + getFrom(o).getTraceId());
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o, @Advice.Return RPC ret) {
            if (o instanceof SumMRTask) {
                TraceContext tc = TraceContext.getOrCreateFrom(o);
                if (ret == null) {
                    tc.getCurrentSpan().add("target", "local node");
                } else {
                    tc.getCurrentSpan().add("target", ret._target.getIpPortString());
                    tc.getCurrentSpan().add("RPC size", ret._dt.asBytes().length);
                }

                tc.closeCurrentSpan();
                System.out.println("Remote compute exit: " + H2O.getIpPortString() + " trace ID " + getCurrent().getTraceId());
            }
        }
    }

    public static class compute2 {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                MRTask tsk = (MRTask) o;

               // TraceContext.getOrCreateFrom(o)
               //         .openNestedSpan("MRTask local work : " + (tsk._hi - tsk._lo));
               // System.out.println("Compute2 (= Local Work) entered. Node: " + H2O.getIpPortString() + " trace ID: " + getCurrent().getTraceId());
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
               // TraceContext.getOrCreateFrom(o).closeCurrentSpan();
               // System.out.println("Compute2 (= Local Work) exit. Node: " + H2O.getIpPortString() + " trace ID: " + getCurrent().getTraceId());
            }
        }

        public static class onCompletion {
            @Advice.OnMethodExit
            public static void exit(@Advice.This Object o) {
                if (o instanceof SumMRTask) {
                    //if(InstrumentUtils.getCurrent().getCurrentSpan().getLongValue("RPC Called") != null) {
                    //    InstrumentUtils.getCurrentSpan().save(); // save without going one level up
                    //}else{
                    //getCurrent().closeCurrentSpan();
                    //}
                }
            }
        }
    }
}
