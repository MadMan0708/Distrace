package water;

import cz.cuni.mff.d3s.distrace.tracing.TraceContext;
import net.bytebuddy.asm.Advice;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class JobAdvices {

  public static class get {
    @Advice.OnMethodExit
    public static void exit(@Advice.This Object o) {
      TraceContext tc = TraceContext.getFromCurrentThreadOrNull();
      if (tc != null && tc.getCurrentSpan().hasFlag("gbm")) {
        TraceContext.getFromCurrentThread().closeCurrentSpan();
        String traceUrl = "http://localhost:9411/zipkin/api/v1/trace/" + tc.getTraceId();

        System.out.println("Download Traces from: " + traceUrl);
        try {
          URL url = new URL(traceUrl);

          URLConnection con = url.openConnection();
          InputStream in = con.getInputStream();
          String encoding = con.getContentEncoding();
          encoding = encoding == null ? "UTF-8" : encoding;
          String body = IOUtils.toString(in, encoding);
          System.out.println("Here");

          BufferedWriter writer = new BufferedWriter(new FileWriter(tc.getTraceId() + ".txt"));
          writer.write(body);
          System.out.println("Saved traces to: " + new File(tc.getTraceId() + ".txt").getAbsolutePath());
        } catch (IOException e) {
          e.printStackTrace();
        }

        System.out.println("Job finished!!!!");
      }
    }
  }

}
