package cz.cuni.mff.d3s.distrace.tracing;

import java.util.HashMap;

/**
 * This class is used to connect a thread with a trace context without using thread local variables.
 */
class TraceContextManager {

    private HashMap<Long, TraceContext> contexts = new HashMap<>();

    /**
     * Attach {@link TraceContext} to the provided thread
     *
     * @param thread  to which attach given trace context
     * @param context trace context to be attached
     * @return context attached context
     */
    synchronized TraceContext attachTraceContextTo(Thread thread, TraceContext context) {
        contexts.put(thread.getId(), context);
        return context;
    }

    /**
     * Get trace context attached to the provided thread or throw NullPointerException if no such mapping
     * can be found. Method {@code getAndAttachFrom} should be used to get or create new trace context.
     *
     * @param thread from which to get trace context
     * @return trace context
     * @throws NullPointerException in case trace context doesn't contain the mapping
     */
    synchronized TraceContext getTraceContext(Thread thread) {
        if (hasTraceContext(thread)) {
            return contexts.get(thread.getId());
        } else {
            throw new NullPointerException("Trace context needs to be available for thread " + thread);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("TraceContextManager{\n");
        for (long key : contexts.keySet()) {
            str.append("  ")
                    .append(key)
                    .append(" : ")
                    .append(contexts.get(key))
                    .append("\n");
        }
        str.append("}");
        return str.toString();
    }

    private boolean hasTraceContext(Thread thread) {
        return contexts.containsKey(thread.getId());
    }

}
