package hex.tree.gbm;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;
import water.H2O;

public class SharedTreeDriverAdvices {

    public static class computeImpl {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            TraceContext.getFromObject(o)
                    .openNestedSpan("computeImpl")
                    .setIpPort(H2O.getIpPortString());


            System.out.println("computeImpl called");
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            TraceContext.getFromObject(o).closeCurrentSpan();

            System.out.println("computeImpl finished");
        }
    }

    public static class scoreAndBuildTrees {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            TraceContext.getFromObject(o)
                    .openNestedSpan("Scoring and Building Trees")
                    .setIpPort(H2O.getIpPortString());

            System.out.println("scoreAndBuildTrees called.");
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            TraceContext.getFromObject(o).closeCurrentSpan();

            System.out.println("scoreAndBuildTrees finished.");
        }



    }


}
