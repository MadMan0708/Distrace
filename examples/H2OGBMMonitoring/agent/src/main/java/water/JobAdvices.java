package water;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;

public class JobAdvices {

  public static class get {
    @Advice.OnMethodExit
    public static void exit(@Advice.This Object o) {
      TraceContext tc = TraceContext.getFromCurrentThreadOrNull();
      if( tc != null && tc.getCurrentSpan().hasFlag("gbm")) {
        TraceContext.getFromCurrentThread().closeCurrentSpan();
        System.out.println("Job finished!!!!");
      }
    }
  }

}
