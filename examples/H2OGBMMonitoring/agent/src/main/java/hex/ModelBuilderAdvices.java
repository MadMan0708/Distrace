package hex;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;
import water.H2O;

public class ModelBuilderAdvices {


  public static class trainModel {
    @Advice.OnMethodEnter
    public static void enter(@Advice.This Object o) {
      TraceContext.create().attachOnCurrentThread()
              .openNestedSpan("The whole training process")
              .setIpPort(H2O.getIpPortString())
              .addFlag("gbm");

      System.out.println("Train method called!");
    }
  }
}
