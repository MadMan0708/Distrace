package cz.cuni.mff.d3s.distrace.api;

import java.util.Random;

/**
 * Per thread context containing information about the trace, span and parent span id
 */
public class TraceContext {

    private int spanId;
    private long traceId = 0;

    public TraceContext(){
        traceId = Math.abs(new Random().nextLong());
        // TODO: improve trace id generation, right it it's super simple and will work only in single
        // TODO: JVM
    }
       public long getTraceId(){
           return traceId;
       }
}
