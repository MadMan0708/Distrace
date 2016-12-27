package cz.cuni.mff.d3s.distrace.api;


import cz.cuni.mff.d3s.distrace.storage.SpanSaver;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Span implements Serializable{

    private Span parentSpan = null;
    private static SpanSaver saver;

    static {
        saver = SpanSaver.fromString(getSaverType());
    }

    private static native String getSaverType();

    private HashMap<String, String> binaryAnnotations = new HashMap<>();
    private long traceId;
    private long spanId;
    private long timestamp;
    private long duration;
    private String serviceName;
    private String name = "Unknown";
    public Span setName(String name){
        this.name = name;
        return this;
    }

    public Span setServiceName(String serviceName){
        this.serviceName = serviceName;
        return this;
    }

    private Span(long traceId, String name, long nextSpanId) {
        this.traceId = traceId;
        this.parentSpan = null;
        this.spanId = nextSpanId;
        this.name = name;
        this.timestamp = System.nanoTime() / 1000;
    }

    private Span(long traceId, Span parentSpan, String name, long nextSpanId) {
        this.traceId = traceId;
        this.spanId = nextSpanId;
        this.timestamp = System.nanoTime() / 1000;
        this.parentSpan = parentSpan;
        this.name = name;
    }

    public void store(){
        duration = System.nanoTime() / 1000 - timestamp / 1000;
        saver.saveSpan(this);
    }

    private long getParentSpanId(){
        if(parentSpan == null){
            // parent span ID = 0 means no parent span ID
            return 0;
        }else{
            return parentSpan.getSpanId();
        }
    }

    public Span getParentSpan(){
        return parentSpan;
    }

    public long getSpanId(){
        return spanId;
    }

    private String parentIdEntry(){
        if(parentSpan != null){
            return "\"parentId\":\"" + getParentSpanId() + "\",\n";
        }else{
            return "";
        }
    }

    private String singleAnnotationAsJSON(String key, String value){
        return "{\n" +
                "\"key\":\"" + key + "\",\n" +
                "\"value\":\"" + value + "\",\n" +
                "\"endpoint\":{\n" +
                    "\"serviceName\":\"" + serviceName + "\"\n" +
                    "}\n" +
                "}\n";
    }

    public Long getLongValue(String key){
        try{
            if(binaryAnnotations.get(key) == null){
                return null;
            }else {
                return Long.parseLong(binaryAnnotations.get(key));
            }
        }catch (NumberFormatException e){
            throw new RuntimeException("It is expected that the key is long");
        }
    }

    private String binaryAnnotationJSON(){
       StringBuilder str = new StringBuilder("\"binaryAnnotations\":[\n");

        int processed = 0;
        for(String annontationName: binaryAnnotations.keySet()){
            str.append(singleAnnotationAsJSON(annontationName, binaryAnnotations.get(annontationName)));
            processed++;
            if(processed<binaryAnnotations.size()){
                str.append(",");
            }
        }

        str.append("]\n");

        return str.toString();
    }

    public Span add(String key, String value){
        binaryAnnotations.put(key, value);
        return this;
    }

    public Span add(String key, long value){
        binaryAnnotations.put(key, value + "");
        return this;
    }

    public Span add(String key, int value){
        binaryAnnotations.put(key, value + "");
        return this;
    }

    public String toJSON() {
        return "[\n" +
                "{\n" +
                "\"traceId\":\"" + traceId + "\",\n" +
                "\"name\":\"" + name + "\",\n" +
                "\"id\":\"" + spanId + "\",\n" +
                parentIdEntry() +
                "\"timestamp\":" + timestamp + ",\n" +
                "\"duration\":" + duration + ",\n" +
                binaryAnnotationJSON() +
                "}\n" +
                "]";
    }

    public static Span newTopSpan(long traceId, String name, long nextSpanId) {
        return new Span(traceId, name, nextSpanId);

    }

    public static Span newNestedSpan(long traceId, Span parentSpan, String name, long nextSpanId) {
        return new Span(traceId, parentSpan, name, nextSpanId);
    }
}
