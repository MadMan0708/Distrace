package cz.cuni.mff.d3s.distrace.utils;

/**
 * Created by kuba on 14/03/16.
 */
public class API {
    public long initSpan(long traceId){
        return 7777;
    }

    public void finishSpan(long traceId, long spanId){
    }

    public long initTrace(){
        return 7777;
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
