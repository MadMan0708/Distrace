package water;

import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;
import net.bytebuddy.asm.Advice;


public class RPCAdvices {

    public static class call {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            RPC rpc = (RPC) o;
            if(rpc._dt instanceof SumMRTask){
                InstrumentUtils.getCurrentSpan().add("RPC Called", (System.nanoTime() / 1000));
            }
        }
    }

}
