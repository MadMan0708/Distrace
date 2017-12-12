package hex.tree.gbm;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;

public class GBMAdvices {

    public static class trainModelImpl {

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o, @Advice.Return Object ret) {
            TraceContext.getFromCurrentThread().attachOnObject(ret);
            System.out.println("Trace Context Attached on " + ret.getClass());
        }

    }

}
