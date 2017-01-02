package cz.cuni.mff.d3s.distrace.tracing;

import java.util.HashMap;

/**
 * This class is used to connect thread with a trace context without using thread locals
 */
public class TraceContextManager {

    private static TraceContextManager instance;
    private HashMap<Long, TraceContext> contexts = new HashMap<>();
    private TraceContextManager() { }

    /**
     * Static method to obtain singleton instance
     * @return singleton instance
     */
    public static TraceContextManager getOrCreate(){
        if(instance == null){
            instance = new TraceContextManager();
        }
        return instance;
    }

    /**
     * Attach {@link TraceContext} to the provided thread
     * @param thread to which attach given trace context
     * @param context trace context to be attached
     */
    public void attachTraceContextTo(Thread thread, TraceContext context){
        contexts.put(thread.getId(), context);
    }

    /**
     * Get trace context attached to the provided thread or throw NullPointerException if no such mapping
     * can be found. Method {@code getOrCreateTraceContext} should be used to get or create new trace context.
     * @param thread from which to get trace context
     * @throws NullPointerException in case trace context doesn't contain the mapping
     * @return trace context
     */
    public TraceContext getTraceContext(Thread thread){
        if(hasTraceContext(thread)){
            return contexts.get(thread.getId());
        }else{
            throw new NullPointerException("Trace context needs to be available for thread " + thread);
        }
    }

    /**
     * Create new {@link TraceContext} attached to provided thread
     * @param thread thread to which attach new trace context
     * @return existing or new trace context
     */
    public TraceContext getOrCreateTraceContext(Thread thread){
        if(!hasTraceContext(thread)){
            attachTraceContextTo(thread, new TraceContext());
        }
        return getTraceContext(thread);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("TraceContextManager{\n");
        for(long key : contexts.keySet()){
            str.append("  ")
                    .append(key)
                    .append(" : ")
                    .append(contexts.get(key))
                    .append("\n");
        }
        str.append("}");
        return str.toString();
    }
    
    private boolean hasTraceContext(Thread thread){
        return contexts.containsKey(thread.getId());
    }

}
