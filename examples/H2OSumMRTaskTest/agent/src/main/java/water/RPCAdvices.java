package water;

import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.instrumentation.StorageUtils;
import cz.cuni.mff.d3s.distrace.instrumentation.StackTraceUtils;
import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;


public class RPCAdvices {

    public static class call {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This RPC thizz) {
            if (StackTraceUtils.containsMethodCall("remote_compute")) {
                if (thizz._dt instanceof SumMRTask) {
                    TraceContext tc = TraceContext.getCopyWithoutAttachFrom(thizz._dt);
                    tc.openNestedSpan("H2O Node" + H2O.SELF.index() + " - Remote work")
                            .setIpPort(H2O.getIpPortString())
                            .add("ipPort", H2O.getIpPortString())
                            .add("RPC Called", System.nanoTime() / 1000);
                    StorageUtils.getList("remote_compute").add(thizz._dt);

                    System.out.println("CALLED " + thizz._dt + " ");
                    tc.attachOnObject(thizz._dt);
                    System.out.println("ADDING" + thizz._dt + "TO storage3");
                    System.out.println("Remote compute enter: " + H2O.getIpPortString() + " trace ID " + tc.getTraceId());
                }
            }
        }
    }

    public static class response {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This RPC thizz) {
            if (thizz._dt instanceof SumMRTask) {
                if ( StorageUtils.getList("remote_compute").contains(thizz._dt)) {
                    StorageUtils.getList("remote_compute").remove(thizz._dt);
                    TraceContext tc = TraceContext.getWithoutAttachFrom(thizz._dt);
                        System.out.println("TRY COMPLETE CALLED STORAGE 333" + thizz._dt) ;
                        tc.getCurrentSpan().appendToName(" __try_complete called");
                        tc.closeCurrentSpan();
                    }
            }
        }
    }



}
