package cz.cuni.mff.d3s.distrace.tracing;


import cz.cuni.mff.d3s.distrace.json.JSONArray;
import cz.cuni.mff.d3s.distrace.json.JSONObject;
import cz.cuni.mff.d3s.distrace.json.JSONValue;
import cz.cuni.mff.d3s.distrace.storage.SpanSaver;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static cz.cuni.mff.d3s.distrace.utils.NativeAgentUtils.getTypeOneUUID;

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

    private void setStackTrace(String type, Thread thread){
        binaryAnnotations.put(type+" stacktrace", getStackTraceAsJSON(thread).toString());
    }

    public void setOpenStackTrace(Thread thread){
        setStackTrace("opening", thread);
    }

    public void setCloseStackTrace(Thread thread){
        setStackTrace("closing", thread);
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

    private String traceId;
    private String spanId;
    private long timestamp;
    private long duration;
    private String serviceName = "Unknown";
    private String name = "Unknown";
    private int port = 0;
    private String ipv4;

    public Span setName(String name){
        this.name = name;
        return this;
    }



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

    public Span appendToName(String append){
        this.name = this.name + append;
        return this;
    }

    private Span(String traceId, String name) {
        this.traceId = traceId;
        this.parentSpan = null;
        this.name = name;
        this.timestamp = System.nanoTime() / 1000;
        this.spanId = Long.toHexString(new Random().nextLong());
        addOriginStartAnn(timestamp);
    }

    Span(Span span){
        this.traceId = span.traceId;
        this.spanId = span.spanId;
        this.timestamp = span.timestamp;
        // it is not required to copy this field as the nested spans should not
        // go above the level of thi span
        this.parentSpan = span.parentSpan == null ? null :  new Span(span.parentSpan);
        this.name = span.name;
        this.annotations = annotationCopy(span.annotations);
        this.binaryAnnotations = annotationCopy(span.binaryAnnotations);
    }

    private static <K,V> HashMap<K, V> annotationCopy(HashMap<K, V> annotations){
        HashMap<K, V> map = new HashMap<>();
        for(Map.Entry<K, V> entry : annotations.entrySet()){
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private Span(String traceId, Span parentSpan, String name) {
        this.traceId = traceId;
        this.spanId = Long.toHexString(new Random().nextLong());
        this.timestamp = System.nanoTime() / 1000;
        this.parentSpan = parentSpan;
        this.name = name;
        addOriginStartAnn(timestamp);
    }

    public String getTraceId(){
        return traceId;
    }

    public String getName(){
        return name;
    }

    public void save(){
        long time = System.nanoTime() / 1000;
        addTargetReceivedAnn(time);
        duration = time - (timestamp / 1000);
        saver.saveSpan(this);
    }

    public String getParentSpanId(){
        if(parentSpan == null){
            // parent span ID = 0 means no parent span ID
            return "0";
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

    public String getSpanId(){
        return spanId;
    }

    public String getStringValue(String key){
        return binaryAnnotations.get(key);
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

    public boolean hasAnnotation(String key){
        return binaryAnnotations.containsKey(key);
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

    public Span add(String key, boolean value){
        binaryAnnotations.put(key, value ? "true" : "false");
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
                .add("parentId", getParentSpanId())
                .add("timestamp", timestamp)
                .add("duration", duration)
                .add("binaryAnnotations", getBinaryAnnotationsJSON())
                .add("annotations", getAnnotationsJSON());

        return new JSONArray(jsonSpan);
    }



    public static Span newTopSpan(String traceId, String name) {
        return new Span(traceId, name);

    }

    public static Span newNestedSpan(String traceId, Span parentSpan, String name) {
        return new Span(traceId, parentSpan, name);
    }
}
