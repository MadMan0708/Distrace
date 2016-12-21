package cz.cuni.mff.d3s.distrace.api;

import cz.cuni.mff.d3s.distrace.utils.InstrumentUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

/**
 * Per thread context containing information about the trace, span and parent span id
 */
public class TraceContext implements Serializable{
    private Span span;
    private long traceId = 0;
    private long nextSpanId = 0;
    private TraceContext(long traceId){
        this.traceId = traceId;
    }

    private HashMap<Long, Span> spans = new HashMap<>();
    public TraceContext(TraceContext context){
        this.span = context.getCurrentSpan();
        this.traceId = context.traceId;
    }

    public TraceContext(){
        traceId = Math.abs(new Random().nextLong());
        // TODO: improve trace id generation, right now it's super simple and will work only in single
        // TODO: JVM
    }

    public void storeSpan(long spanId){
        spans.get(spanId).store();
    }

       public long getTraceId(){
           return traceId;
       }

       public Span getCurrentSpan(){
           return span;
       }


       public Span openNestedSpan(){
           return openNestedSpan("Unknown");
       }

       public Span openSpan(String name){
           nextSpanId = nextSpanId + 1;
           Span s = Span.newNestedSpan(traceId, span, name, nextSpanId);
           spans.put(s.getSpanId(), s);
           return s;
       }

       public Span openNestedSpan(String name){
           if(span == null){
               // parent span
               // create new Span when a new Trace is created
               span = Span.newTopSpan(traceId, name, nextSpanId);
           }else{
               nextSpanId = nextSpanId + 1;
               span = Span.newNestedSpan(traceId, span, name, nextSpanId);
           }
           System.out.println("Opening span, trace id" + traceId + " span id" + span.getSpanId() + " thread id " + Thread.currentThread().getId() + " TraceContext" + this);

           return span;
       }

       public TraceContext storeCurrentSpan(){
           System.out.println("Closing span, trace id" + traceId + " span id" + span + " thread id " + Thread.currentThread().getId() + " TraceContext" + this);

           span.store();
           span = span.getParentSpan();
           return this;
       }

       public static TraceContext from(TraceContext traceContext){
           return new TraceContext(traceContext);
       }
}
