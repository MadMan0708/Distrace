package water;

import cz.cuni.mff.d3s.distrace.examples.SumMRTask;
import net.bytebuddy.asm.Advice;

import static cz.cuni.mff.d3s.distrace.utils.InstrumentUtils.getTraceContext;

/**
 * Created by kuba on 18/12/2016.
 */
public class RemoteComputeAdvice {

    @Advice.OnMethodExit
    public static Object remote_compute(@Advice.This Object o, @Advice.BoxedReturn Object ret){
        if (o instanceof SumMRTask) {
            if(ret == null){
                System.out.println("No remote work");
            }else{
                try {
                    H2ONode node = (H2ONode) ret.getClass().getDeclaredField("_target").get(ret);
                    System.out.println("Computation planned on " + node.getIpPortString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Remote compute was called on node: " + H2O.getIpPortString() + " trace ID " +  getTraceContext(o).getTraceId());
        }
        return ret;
    }
}
