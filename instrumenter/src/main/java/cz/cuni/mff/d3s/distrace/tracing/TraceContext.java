package cz.cuni.mff.d3s.distrace.tracing;

import cz.cuni.mff.d3s.distrace.instrumentation.InstrumentUtils;
import cz.cuni.mff.d3s.distrace.utils.NativeAgentUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Per thread context containing information about the trace, span and parent span id
 */
public class TraceContext implements Serializable {
    private Span span;
    private String traceId;

    private TraceContext(TraceContext context) {
        this.span = context.getCurrentSpan();
        this.traceId = context.traceId;
    }

    public TraceContext() {
        traceId = NativeAgentUtils.getTypeOneUUIDHex();
    }

    public String getTraceId() {
        return traceId;
    }

    /**
     * Get current span
     * @return current span
     */
    public Span getCurrentSpan() {
        return span;
    }

    /**
     * Create a new span and move one level down in span hierarchy
     * @return created span
     */
    public Span openNestedSpan() {
        return openNestedSpan("Unknown");
    }

    /**
     * Create a new span and move one level down in span hierarchy
     * @param name span name
     * @return trace context
     */
    public Span openNestedSpan(String name) {
        if (span == null) {
            // parent span
            // create new Span when a new Trace is created
            span = Span.newTopSpan(traceId, name);
        } else {
            span = Span.newNestedSpan(traceId, span, name);
        }
        span.setStackTrace(Thread.currentThread());
        System.out.println("Opening span, trace id" + traceId + " span id" + span.getSpanId() + " thread id " + Thread.currentThread().getId() + " TraceContext" + this);
        return span;
    }

    /**
     * Store current span and move one level up in span hierarchy
     * @return trace context
     */
    public TraceContext closeCurrentSpan() {
        System.out.println("Closing span, trace id" + traceId + " span id" + span + " thread id " + Thread.currentThread().getId() + " TraceContext" + this);
        span.save();
        span = span.getParentSpan();
        return this;
    }

    /**
     * Create new Trace context from existing trace context
     * @param traceContext trace context from which to create a new trace context
     * @return new trace context
     */
    public static TraceContext from(TraceContext traceContext) {
        return new TraceContext(traceContext);
    }

    public static TraceContext createAndAttachTo(Object o) {
        InstrumentUtils.contextManager.attachTraceContextTo(Thread.currentThread(), new TraceContext());
        InstrumentUtils.attachTraceContextOn(o, InstrumentUtils.contextManager.getTraceContext(Thread.currentThread()));
        return InstrumentUtils.contextManager.getTraceContext(Thread.currentThread());
    }

    public static TraceContext getFrom(Object traceContextHolder) {
        try {
            Field f = traceContextHolder.getClass().getDeclaredField(InstrumentUtils.traceIdFieldName);
            f.setAccessible(true);
            return (TraceContext)f.get(traceContextHolder);
        } catch (IllegalAccessException | NoSuchFieldException e1) {
            return null;
        }
    }

    public static TraceContext getOrCreateFrom(Object traceContextHolder) {
        if(getFrom(traceContextHolder) == null){
            throw new RuntimeException("No such field " + InstrumentUtils.traceIdFieldName + " field should be part of the class " + traceContextHolder.getClass());
        }
        InstrumentUtils.contextManager.attachTraceContextTo(Thread.currentThread(), getFrom(traceContextHolder));
        return InstrumentUtils.contextManager.getTraceContext(Thread.currentThread());
    }

    public static TraceContext getCurrent() {
        return InstrumentUtils.contextManager.getTraceContext(Thread.currentThread());
    }

}
