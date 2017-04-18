package water;

import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.instrumentation.StorageUtils;
import cz.cuni.mff.d3s.distrace.instrumentation.StackTraceUtils;
import cz.cuni.mff.d3s.distrace.tracing.Span;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import jsr166y.CountedCompleter;
import net.bytebuddy.asm.Advice;


public abstract class MRTaskAdvices {

    public static class dfork {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                TraceContext.createAndAttachTo(o)
                        .openNestedSpan("H2O Node"+H2O.SELF.index() + " - Complete MRTask Computation")
                        .setIpPort(H2O.getIpPortString())
                        .add("ipPort", H2O.getIpPortString());

                System.out.println("doAll: Method was called on node: " + H2O.getIpPortString() + " trace ID " + TraceContext.getAndAttachFrom(o).getTraceId() + " thread id " + Thread.currentThread().getId() + " Span id = "+ TraceContext.getAndAttachFrom(o).getCurrentSpan().getSpanId());
            }
        }
    }

    public static class getResult {
        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                TraceContext tc = TraceContext.getAndAttachFrom(o);
                System.out.println("getResult: Storing Span with ID: " + tc.getCurrentSpan().getSpanId());
                tc.closeCurrentSpan();
            }
        }
    }

    public static class setupLocal0 {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                TraceContext tc = TraceContext.getWithoutAttachFrom(o);
                tc.openNestedSpan( "H2O Node"+H2O.SELF.index() + " - Setting and Splitting on " + H2O.getIpPortString())
                        .setIpPort(H2O.getIpPortString());
                tc.getCurrentSpan().add("setupLocal0 entry", o.toString());
                tc.attachOnObject(o);
                StorageUtils.getList("setupLocal").add(o);
                System.out.println("SETUPLOCAL0 " + o.hashCode() + " span id = " + tc.getCurrentSpan().getSpanId() + " parent id = " + tc.getCurrentSpan().getParentSpan().getSpanId());

            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                System.out.println("SetupLocal0 ( dist prepare) was called on node: " + H2O.getIpPortString() + " trace ID " + TraceContext.getAndAttachFrom(o).getTraceId());
            }
        }
    }

    public static class remote_compute {

        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o,  @Advice.Argument(0) Integer nlo,  @Advice.Argument(1) Integer nhi) {
            if (o instanceof SumMRTask) {
                if(nlo >= nhi){
                    TraceContext tc = TraceContext.getCopyWithoutAttachFrom(o);
                    tc.openNestedSpan("H2O Node"+H2O.SELF.index() + " - Remote Work - none")
                            .setIpPort(H2O.getIpPortString());

                    tc.attachOnObject(o);
                }
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o, @Advice.Return RPC ret) {
            if (o instanceof SumMRTask) {
                if (ret == null) {
                    TraceContext.getWithoutAttachFrom(o).closeCurrentSpan();
                }

            }
        }
    }

    public static class compute2 {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            if (o instanceof SumMRTask) {
                MRTask tsk = (MRTask) o;
                System.out.println("COMPUTE2 " + o.hashCode());
                TraceContext tc = TraceContext.getCopyWithoutAttachFrom(o);
                tc.openNestedSpan("H2O Node" + H2O.SELF.index() + " - Local work - chunks : " + (tsk._hi - tsk._lo));
                tc.getCurrentSpan().add("compute2 entry", o.toString());
                tc.attachOnObject(o);
                System.out.println("ADDING " + o + " to Storage2");
                StorageUtils.getList("compute2").add(o);
                System.out.println("Compute2 (= Local Work) entered. Node: " + H2O.getIpPortString() + " trace ID: " + tc.getTraceId());

                if (StackTraceUtils.numMethodCalls("compute2") >= 2) {
                    tc.getCurrentSpan().appendToName(" - forked");
                }
            }
        }
    }

    public static class reduce2 {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This MRTask thizz, @Advice.Argument(0) MRTask mrt){
            if (thizz instanceof SumMRTask) {
                if(mrt != null){
                    String str;
                    if(thizz._left == null){
                        str = "right";
                    }else if(thizz._rite == null){
                        str = "left";
                    }else{
                        str = thizz._left.equals(mrt) ? "left" : "right";
                    }
                    TraceContext tc = TraceContext.getWithoutAttachFrom(thizz);
                    tc.openNestedSpan("H2O Node"+H2O.SELF.index() + " - Reducing " + str);
                    tc.getCurrentSpan().add("start", System.nanoTime() / 1000);
                }

            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This MRTask thizz, @Advice.Argument(0) MRTask mrt){
            if(mrt != null) {
                TraceContext tc = TraceContext.getWithoutAttachFrom(thizz);
                tc.getCurrentSpan().add("end", System.nanoTime() / 1000);
                Span s = tc.getCurrentSpan();
                tc.getCurrentSpan().appendToName(" - lasted - " + (s.getLongValue("end")-s.getLongValue("start")) + " ms");
                tc.closeCurrentSpan();
            }
        }
    }


    public static class map {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This MRTask thizz){
            if (thizz instanceof SumMRTask) {
                    TraceContext tc = TraceContext.getWithoutAttachFrom(thizz);
                    tc.openNestedSpan("H2O Node"+H2O.SELF.index() + " - mapping ");
                    tc.getCurrentSpan().add("start", System.nanoTime() / 1000);
            }
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This MRTask thizz){
                TraceContext tc = TraceContext.getWithoutAttachFrom(thizz);
                tc.getCurrentSpan().add("end", System.nanoTime() / 1000);
                Span s = tc.getCurrentSpan();
                tc.getCurrentSpan().appendToName(" - lasted - " + (s.getLongValue("end")-s.getLongValue("start")) + " ms");
                tc.closeCurrentSpan();
        }
    }

    public static class onCompletion {
        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o, @Advice.Argument(0) CountedCompleter caller) {
            if (o instanceof SumMRTask) {
                MRTask task = (MRTask) o;
                TraceContext tc = TraceContext.getWithoutAttachFrom(task);

                if(StorageUtils.getList("compute2").contains(task)){
                    StorageUtils.getList("compute2").remove(task);
                    tc.getCurrentSpan().add("compute2 exit", task.toString());
                    tc.closeCurrentSpan();
                    System.out.println("OnCompletion exit compute2: " + H2O.getIpPortString() + " trace ID " +tc.getTraceId() + " task id " + task.hashCode());
                }
                // setupLocal0 span finishes when there are no more pending task on this node for this MRTask
                if(StorageUtils.getList("setupLocal").contains(task)){
                    StorageUtils.getList("setupLocal").remove(task);
                    tc.getCurrentSpan()
                            .add("left", task._nleft == null ? "local" : task._nleft._target.getIpPortString())
                            .add("right", task._nrite == null ? "local" : task._nrite._target.getIpPortString());

                    tc.getCurrentSpan().add("setupLocal0 exit", task.toString())
                            .add("caller", caller.toString());
                    tc.closeCurrentSpan();
                    System.out.println("OnCompletion exit setupLocal0: " + H2O.getIpPortString() + " trace ID " + tc.getTraceId() + " task id " + task.hashCode());

                }

            }
        }
    }
}
