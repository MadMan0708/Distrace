package cz.cuni.mff.d3s.distrace.api;

import oracle.jrockit.jfr.StringConstantPool;

import java.util.UUID;

/**
 * Main API for creating spans and traces
 */
public class API {
    /**
     * Initializes a Span. Span represents a small unit of work
     */
    public long openSpan(String traceId){
        return 7777;
    }

    /**
     *
     * @param traceId
     * @param spanId
     */
    public void closeSpan(long traceId, long spanId){
    }

    /**
     * Initialize new trace. This method create trace context in the thread which called this method
     * @return ID of created trace
     */
    public static String initTrace(){
        return UUID.randomUUID().toString();
    }


    public int  finishTrace(long traceId){
        return 7777;
    }

    public long getCurrentSpan(){
        return 7777;
    }

    public long getCurrentTrace(){
        return 7777;
    }
}
