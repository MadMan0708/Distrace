package cz.cuni.mff.d3s.distrace.tracing;


import cz.cuni.mff.d3s.distrace.json.JSONArray;
import cz.cuni.mff.d3s.distrace.json.JSONObject;
import cz.cuni.mff.d3s.distrace.json.JSONValue;
import cz.cuni.mff.d3s.distrace.storage.SpanSaver;

import java.io.Serializable;
import java.util.HashMap;

public class Span implements Serializable {

    private Span parentSpan = null;
    private static SpanSaver saver;

    static {
        saver = SpanSaver.fromString(getSaverType());
    }

    private JSONArray getStackTraceAsJSON(Thread thread){
        JSONArray stackTrace = new JSONArray();
        // skip first 4 elements since they contain always calls to method getStackTraceAsJSON an the rest of span related
        // calls
        StackTraceElement[] elements = thread.getStackTrace();
        for(int i = 4; i<elements.length; i++){
            stackTrace.add(elements[i].toString());
        }
        return stackTrace;
    }

    public void setStackTrace(Thread thread){
        binaryAnnotations.put("stacktrace", getStackTraceAsJSON(thread).toString());
    }

    private static native String getSaverType();

    private HashMap<String, String> binaryAnnotations = new HashMap<>();
    private HashMap<String, Long> annotations = new HashMap<>();


    private Span addOriginStartAnn(long timestamp){
        annotations.put("cs", timestamp);
        return this;
    }

    private Span addTargetReceivedAnn(long timestamp){
        annotations.put("sr", timestamp);
        return this;
    }

    private Span addTargetSentAnn(long timestamp){
        annotations.put("ss", timestamp);
        return this;
    }

    private Span addOriginReceivedAnn(long timestamp){
        annotations.put("cr", timestamp);
        return this;
    }

    private long traceId;
    private long spanId;
    private long timestamp;
    private long duration;
    private String serviceName = "Unknown";
    private String name = "Unknown";
    public Span setName(String name){
        this.name = name;
        return this;
    }

    int port = 0;
    String ipv4;

    public Span setServiceName(String serviceName){
        this.serviceName = serviceName;
        return this;
    }

    public Span setIpPort(String ipPort){
        final String[] split = ipPort.split(":");
        ipv4 = split[0];
        port = Integer.parseInt(split[1]);
        return this;
    }

    public Span setIp(String ip){
        ipv4 = ip;
        return this;
    }

    public Span setPort(int port){
        this.port = port;
        return this;
    }

    private Span(long traceId, String name, long nextSpanId) {
        this.traceId = traceId;
        this.parentSpan = null;
        this.spanId = nextSpanId;
        this.name = name;
        this.timestamp = System.nanoTime() / 1000;
        addOriginStartAnn(timestamp);
    }

    private Span(long traceId, Span parentSpan, String name, long nextSpanId) {
        this.traceId = traceId;
        this.spanId = nextSpanId;
        this.timestamp = System.nanoTime() / 1000;
        this.parentSpan = parentSpan;
        this.name = name;
        addOriginStartAnn(timestamp);
    }

    public long getTraceId(){
        return traceId;
    }

    public String getName(){
        return name;
    }

    public void store(){
        duration = System.nanoTime() / 1000 - timestamp / 1000;
        saver.saveSpan(this);
    }

    private Long getParentSpanId(){
        if(parentSpan == null){
            // parent span ID = 0 means no parent span ID
            return null;
        }else{
            return parentSpan.getSpanId();
        }
    }

    public long getTimestamp(){
        return timestamp;
    }

    public Span getParentSpan(){
        return parentSpan;
    }

    public long getSpanId(){
        return spanId;
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

    private JSONObject cachedEndpoint = null;
    private JSONObject getEndpointJSON(){
        if( cachedEndpoint == null) {
            cachedEndpoint = new JSONObject()
                    .add("serviceName", serviceName);
            if(port != 0){
                cachedEndpoint.add("port", port);
            }
            if(ipv4 != null){
                cachedEndpoint.add("ipv4", ipv4);
            }
        }
        return cachedEndpoint;
    }

    private JSONObject getAnnotationJSON(String key, long value){
        return new JSONObject()
                .add("value", key)
                .add("timestamp", value)
                .add("endpoint", getEndpointJSON());
    }

    private JSONObject getBinaryAnnotationJSON(String key, String value){
        return new JSONObject()
                .add("key", key)
                .add("value", value)
                .add("endpoint", getEndpointJSON());
    }

    private JSONArray getBinaryAnnotationsJSON(){
        JSONArray binAnn = new JSONArray();
        for(String name: binaryAnnotations.keySet()){
            binAnn.add(getBinaryAnnotationJSON(name, binaryAnnotations.get(name)));
        }
        return binAnn;
    }

    private JSONArray getAnnotationsJSON(){
        JSONArray ann = new JSONArray();
        for(String name: annotations.keySet()){
            ann.add(getAnnotationJSON(name, annotations.get(name)));
        }
        return ann;
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

    public JSONValue toJSON() {
        JSONObject jsonSpan = new JSONObject()
                .add("traceId", traceId)
                .add("name", name)
                .add("id", spanId)
                .add("timestamp", timestamp)
                .add("duration", duration)
                .add("binaryAnnotations", getBinaryAnnotationsJSON())
                .add("annotations", getAnnotationsJSON())
                .addIfNotNull("parentId", getParentSpanId());

        return new JSONArray(jsonSpan);
    }



    public static Span newTopSpan(long traceId, String name, long nextSpanId) {
        return new Span(traceId, name, nextSpanId);

    }

    public static Span newNestedSpan(long traceId, Span parentSpan, String name, long nextSpanId) {
        return new Span(traceId, parentSpan, name, nextSpanId);
    }
}
