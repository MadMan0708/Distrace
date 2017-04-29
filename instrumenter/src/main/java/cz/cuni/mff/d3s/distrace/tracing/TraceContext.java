package cz.cuni.mff.d3s.distrace.tracing;

import cz.cuni.mff.d3s.distrace.storage.DirectZipkinExporter;
import cz.cuni.mff.d3s.distrace.storage.SpanExporter;
import cz.cuni.mff.d3s.distrace.utils.NativeAgentUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Trace context is a class which contains information about the
 * currently monitored trace. It allows us to create and close spans within this trace.
 */
public class TraceContext implements Serializable {
    private Span span;
    private String traceId;
    private static final String traceContextFieldName = "____traceContext";
    private static final TraceContextManager contextManager = new TraceContextManager();

    private TraceContext(TraceContext context) {
        this.span = context.getCurrentSpan() == null ? null : new Span(context.getCurrentSpan());
        this.traceId = context.traceId;
    }

    /**
     * Get trace identifier
     *
     * @return id
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * Get current span
     *
     * @return current span
     */
    public Span getCurrentSpan() {
        return span;
    }

    /**
     * Create a new span and move one level down in span hierarchy
     *
     * @return created span
     */
    public Span openNestedSpan() {
        return openNestedSpan("Unknown");
    }

    /**
     * Create a new span and move one level down in span hierarchy
     *
     * @param name span name
     * @return trace context
     */
    public Span openNestedSpan(String name) {
        if (span == null) { // if current span is null, then we need to open top-level span first
            // create new top level with the specified trace id and name
            span = Span.newTopSpan(traceId, name);
        } else {
            // create a new nested span
            span = Span.newNestedSpan(traceId, span, name);
        }
        span.setOpenStackTrace(Thread.currentThread());
        printSpanInfo(SpanEvent.OPENING);
        return span;
    }

    /**
     * Process current span using the set {@link SpanExporter} and move
     * one level up in the span hierarchy.
     * If no span exporter is explicitly specified, the default {@link DirectZipkinExporter} is used.
     *
     * @return trace context
     */
    public TraceContext closeCurrentSpan() {
        printSpanInfo(SpanEvent.CLOSING);
        span.setCloseStackTrace(Thread.currentThread());
        span.export();
        span = span.getParentSpan();
        return this;
    }


    public static TraceContext create() {
        return new TraceContext();
    }


    /**
     * Get trace context from the specified holder object.
     * A holder object is used to transfer the trace information between different application nodes.
     *
     * @param traceContextHolder holder object
     * @return trace context attached to the specified holder object
     */
    public static TraceContext getFromObject(Object traceContextHolder) {
        try {
            Field f = traceContextHolder.getClass().getDeclaredField(traceContextFieldName);
            f.setAccessible(true);
            return (TraceContext) f.get(traceContextHolder);
        } catch (IllegalAccessException | NoSuchFieldException e1) {
            throw new RuntimeException("No such field " + traceContextFieldName + " field should be part of the class " + traceContextHolder.getClass());
        }
    }

    /**
     * Get trace context from the specified thread
     *
     * @param thread thread from which to get trace context
     * @return trace context attached to the specified thread
     */
    public static TraceContext getFromThread(Thread thread) {
        return contextManager.getTraceContext(thread);
    }

    /**
     * Get trace context from the current thread
     *
     * @return current trace context
     */
    public static TraceContext getFromCurrentThread() {
        return contextManager.getTraceContext(Thread.currentThread());
    }

    /**
     * Attach trace context on the specified holder object.
     * A holder object is used to transfer the trace information between different application nodes.
     *
     * @param traceContextHolder holder
     * @return trace context
     */
    public TraceContext attachOnObject(Object traceContextHolder) {
        try {
            Field f = traceContextHolder.getClass().getDeclaredField(traceContextFieldName);
            f.setAccessible(true);
            f.set(traceContextHolder, this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Attach trace context to a specified thread
     *
     * @param thread thread
     * @return trace context
     */
    public TraceContext attachOnTread(Thread thread) {
        return contextManager.attachTraceContextTo(thread, this);
    }

    /**
     * Attach trace context to a current thread
     *
     * @return trace context
     */
    public TraceContext attachOnCurrentThread() {
        return contextManager.attachTraceContextTo(Thread.currentThread(), this);
    }

    /**
     * Create new Trace context from existing trace context.
     * This method should be used in cases where a trace context is passes to a different thread within
     * the same node to avoid multiple threads accessing the same context
     *
     * @return new trace context
     */
    public TraceContext deepCopy() {
        return new TraceContext(this);
    }

    /**
     * Internal constructor to create trace context with its unique id.
     * Users should use TraceContext.create() method
     */
    private TraceContext() {
        traceId = NativeAgentUtils.getTypeOneUUIDHex();
    }

    /**
     * Internal method used for debugging of spans creation
     *
     * @param event Span event, can be either closing or opening
     */
    private void printSpanInfo(SpanEvent event) {

        if (!NativeAgentUtils.isDebugging()) {
            // don't print the span info if we are not in the debug mode
            return;
        }

        String parentSpanId = span.getParentSpanId() == null ? "none" : span.getParentSpanId();

        String parentSpan = span.getParentSpan() == null ? "none" : span.getParentSpan().toString();

        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 3; i < elements.length; i++) {
            stringBuilder.append(elements[i].toString()).append("\n");
        }

        System.out.println("\n" + event.toString() + " span:  \n" +
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
     * Small enum representing span events
     */
    public enum SpanEvent {
        OPENING, CLOSING;

        @Override
        public String toString() {
            if (this.equals(OPENING)) {
                return "opening";
            } else if (this.equals(CLOSING)) {
                return "closing";
            } else {
                throw new RuntimeException("Unknown SpanEvent ");
            }
        }
    }
}
