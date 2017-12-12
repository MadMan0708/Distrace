package hex.tree.gbm;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;
import water.H2O;

public class GBMDriverAdvices {


    public static class buildNextKTrees {
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            TraceContext.getFromObject(o)
                    .openNestedSpan("Next K trees")
                    .setIpPort(H2O.getIpPortString());


            System.out.println("Layer creation started!");
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            TraceContext.getFromObject(o).closeCurrentSpan();

            System.out.println("Layer created!");
        }

    }


    public static class initializeModelSpecifics {

        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object o) {
            TraceContext.getFromObject(o)
                    .openNestedSpan("initializeModelSpecifics")
                    .setIpPort(H2O.getIpPortString());

            System.out.println("initializeModelSpecifics called");
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.This Object o) {
            TraceContext.getFromObject(o).closeCurrentSpan();

            System.out.println("initializeModelSpecifics exit");
        }

    }

}



