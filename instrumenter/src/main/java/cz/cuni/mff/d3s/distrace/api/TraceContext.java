package cz.cuni.mff.d3s.distrace.api;

import java.io.Serializable;
import java.util.Random;

/**
 * Per thread context containing information about the trace, span and parent span id
 */
public class TraceContext implements Serializable{
    private Span span;
    private int traceId = 0;

    public TraceContext(TraceContext context){
        this.span = context.getCurrentSpan();
        this.traceId = context.traceId;
    }

    public TraceContext(){
        traceId = Math.abs(new Random().nextInt());
        // create new Span when a new Trace is created
        span = Span.newSpanForTrace(traceId);
        // TODO: improve trace id generation, right now it's super simple and will work only in single
        // TODO: JVM
    }
       public long getTraceId(){
           return traceId;
       }

       public Span getCurrentSpan(){
           return span;
       }

       public TraceContext nestSpan(){
           assert span != null;
           this.span = Span.newNestedSpan(traceId, span.getSpanId());
           return this;
       }
}
