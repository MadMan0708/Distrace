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
                    TraceContext tc = TraceContext.getFromObject(thizz._dt).deepCopy();
                    tc.openNestedSpan("H2O Node" + H2O.SELF.index() + " - Remote work - rpc")
                            .setIpPort(H2O.getIpPortString())
                            .add("ipPort", H2O.getIpPortString());
                    StorageUtils.getList("remote_compute").add(thizz._dt);

                    tc.attachOnObject(thizz._dt);
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
                    TraceContext.getFromObject(thizz._dt).closeCurrentSpan();
                    }
            }
        }
    }

}
