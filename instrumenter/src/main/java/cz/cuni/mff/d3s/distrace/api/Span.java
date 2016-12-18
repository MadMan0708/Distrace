package cz.cuni.mff.d3s.distrace.api;


import cz.cuni.mff.d3s.distrace.storage.SpanSaver;

import java.io.Serializable;

public class Span implements Serializable{

    private static SpanSaver saver;

    static {
        saver = SpanSaver.fromString(getSaverType());
    }

    private static native String getSaverType();
    private long traceId;
    private long parentSpanId;
    private long spanId;
    private long timestamp;
    private long duration;
    private String name = "Unknown";

    public Span setName(String name){
        this.name = name;
        return this;
    }

    private Span(long traceId) {
        this.traceId = traceId;
        this.parentSpanId = 0; // parent span ID = 0 means no parent span ID
        this.spanId = traceId;
        this.timestamp = System.currentTimeMillis();
    }

    private Span(long traceId, long parentSpanId) {
        this.traceId = traceId;
        this.spanId = parentSpanId + 1;
        this.timestamp = System.currentTimeMillis();
        this.parentSpanId = parentSpanId;
    }

    public void store(){
        duration = System.currentTimeMillis() - timestamp;
        saver.saveSpan(this);
    }
    public long getParentSpanId(){
        return parentSpanId;
    }

    public long getSpanId(){
        return spanId;
    }

    public String toJSON() {
        return "[\n" +
                "{\n" +
                "\"traceId\":\"" + traceId + "\",\n" +
                "\"name\":\"" + name + "\",\n" +
                "\"id\":\"" + spanId + "\",\n" +
                "\"parentId\":\"" + parentSpanId + "\",\n" +
                "\"timestamp\":" + timestamp + ",\n" +
                "\"duration\":" + duration + "\n" +
                "}\n" +
                "]";
    }

    public static Span newSpanForTrace(long traceId) {
        return new Span(traceId);

    }

    public static Span newNestedSpan(long traceId, long parentSpanId) {
        return new Span(traceId, parentSpanId);
    }
}
