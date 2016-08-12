package cz.cuni.mff.d3s.distrace;

import cz.cuni.mff.d3s.distrace.api.TraceContext;

import java.util.HashMap;


public class TraceContextManager {
    private static HashMap<Long,TraceContext> contexts = new HashMap<>();
    public static void registerTraceContext(Thread thread, TraceContext context){
        contexts.put(thread.getId(), context);

    }

    public static TraceContext get(Thread thread){
        if(hasTraceContext(thread)){
            return contexts.get(thread.getId());
        }else{
            return null;
        }
    }

    public static boolean hasTraceContext(Thread thread){
        return contexts.containsKey(thread.getId());
    }

    public static TraceContext getOrCreate(Thread thread, TraceContext context){
        if(!hasTraceContext(thread)){
            registerTraceContext(thread, context);
        }
        return get(thread);
    }

}
