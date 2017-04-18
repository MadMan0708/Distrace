package cz.cuni.mff.d3s.distrace.tracing;

import cz.cuni.mff.d3s.distrace.utils.NativeAgentUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Per thread context containing information about the trace, span and parent span id
 */
public class TraceContext implements Serializable {
    private Span span;
    private String traceId;
    private static final String traceContextFieldName = "____traceId";
    private static final TraceContextManager contextManager = TraceContextManager.getOrCreate();

    private TraceContext(TraceContext context) {
        this.span = new Span(context.getCurrentSpan());
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
        span.setOpenStackTrace(Thread.currentThread());
        printSpanInfo("Opening");
        return span;
    }

    /**
     * Store current span and move one level up in span hierarchy
     * @return trace context
     */
    public TraceContext closeCurrentSpan() {
        printSpanInfo("Closing");
        span.setCloseStackTrace(Thread.currentThread());
        span.save();
        span = span.getParentSpan();
        return this;
    }

    private void printSpanInfo(String opType){
        String parentSpanId = span.getParentSpanId() == null ? "none" : span.getParentSpanId();

        String parentSpan = span.getParentSpan() == null ? "none" : span.getParentSpan().toString();

        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for(int i = 3; i<elements.length; i++){
            stringBuilder.append(elements[i].toString()).append("\n");
        }

        System.out.println("\n" + opType + " span:  \n" +
                "        trace id    = " + traceId + "\n" +
                "        parent id   = " + parentSpanId + "\n" +
                "        span id     = " + span.getSpanId() + "\n" +
                "        thread name = " + Thread.currentThread().getName() + "\n" +
                "        context     = " + this + "\n" +
                "        parent span = " + parentSpan + "\n" +
                "        span        = " + span + "\n" +
                "        stack       = \n" + stringBuilder.toString() + "\n");
    }

    /**
     * Create new Trace context from existing trace context
     * @return new trace context
     */
    private TraceContext deepCopy() {
        return new TraceContext(this);
    }

    public static TraceContext createAndAttachTo(Object o) {
        contextManager.attachTraceContextTo(Thread.currentThread(), new TraceContext());
        attachTraceContextOn(o, contextManager.getTraceContext(Thread.currentThread()));
        return contextManager.getTraceContext(Thread.currentThread());
    }

    private static TraceContext getFromHolder(Object traceContextHolder) {
        try {
            Field f = traceContextHolder.getClass().getDeclaredField(traceContextFieldName);
            f.setAccessible(true);
            return (TraceContext)f.get(traceContextHolder);
        } catch (IllegalAccessException | NoSuchFieldException e1) {
                throw new RuntimeException("No such field " + traceContextFieldName + " field should be part of the class " + traceContextHolder.getClass());
        }
    }

    public TraceContext attachOnObject(Object traceContextHolder){
        attachTraceContextOn(traceContextHolder, this);
        return this;
    }

    public TraceContext attachOnTread(Thread thread){
        return contextManager.getOrCreateTraceContext(thread);
    }

    public static TraceContext getAndAttachFrom(Object traceContextHolder) {
        TraceContext tc = getWithoutAttachFrom(traceContextHolder);
        contextManager.attachTraceContextTo(Thread.currentThread(), tc);
        return tc;
    }

    public static TraceContext getWithoutAttachFrom(Object traceContextHolder){
        return getFromHolder(traceContextHolder);
    }

    public static TraceContext getCopyWithoutAttachFrom(Object traceContextHolder) {
        return getWithoutAttachFrom(traceContextHolder).deepCopy();
    }

    public static TraceContext getCurrent() {
        return contextManager.getTraceContext(Thread.currentThread());
    }

    private static  void attachTraceContextOn(Object thizz, TraceContext context) {
        try {
            Field f = thizz.getClass().getDeclaredField(traceContextFieldName);
            f.setAccessible(true);
            f.set(thizz, context);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
