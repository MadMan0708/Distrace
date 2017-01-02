package cz.cuni.mff.d3s.distrace.tracing;

import java.util.HashMap;

/**
 * This class is used to connect thread with a trace context without using thread locals
 */
public class TraceContextManager {

    private static TraceContextManager instance;
    private HashMap<Long,TraceContext> contexts = new HashMap<>();
    private TraceContextManager() { }

    public static TraceContextManager getOrCreate(){
        if(instance == null){
            instance = new TraceContextManager();
        }
        return instance;
    }

    public void registerTraceContext(Thread thread, TraceContext context){
        contexts.put(thread.getId(), context);
    }

    public TraceContext getTraceContext(Thread thread){
        if(hasTraceContext(thread)){
            return contexts.get(thread.getId()); // we need to return new object
        }else{
            return null;
        }
    }

    private boolean hasTraceContext(Thread thread){
        return contexts.containsKey(thread.getId());
    }

    public TraceContext getOrCreateTraceContext(Thread thread, TraceContext context){
        if(!hasTraceContext(thread)){
            registerTraceContext(thread, context);
        }
        return getTraceContext(thread);
    }

    /**
     * Create new {@link TraceContext} attached to provided thread
     * @param thread thread to which attach new trace context
     * @return existing or new trace context
     */
    public TraceContext getOrCreateTraceContext(Thread thread){
        if(!hasTraceContext(thread)){
            registerTraceContext(thread, new TraceContext());
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
}
