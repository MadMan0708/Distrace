package cz.cuni.mff.d3s.distrace.tracing;

import java.io.Serializable;
import java.util.Random;

/**
 * Per thread context containing information about the trace, span and parent span id
 */
public class TraceContext implements Serializable {
    private Span span;
    private long traceId = 0;
    private long nextSpanId = 0;
    private TraceContext(long traceId){
        this.traceId = traceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TraceContext that = (TraceContext) o;

        if (traceId != that.traceId) return false;
        if (nextSpanId != that.nextSpanId) return false;
        return span != null ? span.equals(that.span) : that.span == null;

    }

    @Override
    public int hashCode() {
        int result = span != null ? span.hashCode() : 0;
        result = 31 * result + (int) (traceId ^ (traceId >>> 32));
        result = 31 * result + (int) (nextSpanId ^ (nextSpanId >>> 32));
        return result;
    }

   // private HashMap<Long, Span> spans = new HashMap<>();
    public TraceContext(TraceContext context){
        this.span = context.getCurrentSpan();
        this.traceId = context.traceId;
    }

    public TraceContext(){
        traceId = Math.abs(new Random().nextLong());
        // TODO: improve trace id generation, right now it's super simple and will work only in single
        // TODO: JVM
    }

   // public void storeSpan(long spanId){
   //     spans.getOrCreateTraceContext(spanId).store();
   // }

       public long getTraceId(){
           return traceId;
       }

       public Span getCurrentSpan(){
           return span;
       }


       public Span openNestedSpan(){
           return openNestedSpan("Unknown");
       }

     //  public Span openSpan(String name){
       //    nextSpanId = nextSpanId + 1;
        //   Span s = Span.newNestedSpan(traceId, span, name, nextSpanId);
         //  spans.put(s.getSpanId(), s);
         //  return s;
      // }

       public Span openNestedSpan(String name){
           if(span == null){
               // parent span
               // create new Span when a new Trace is created
               span = Span.newTopSpan(traceId, name, nextSpanId);
           }else{
               nextSpanId = nextSpanId + 1;
               span = Span.newNestedSpan(traceId, span, name, nextSpanId);
           }
           span.setStackTrace(Thread.currentThread());
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
