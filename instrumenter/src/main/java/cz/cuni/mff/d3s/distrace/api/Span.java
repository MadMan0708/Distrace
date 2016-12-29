package cz.cuni.mff.d3s.distrace.api;


import cz.cuni.mff.d3s.distrace.storage.SpanSaver;

import java.io.Serializable;
import java.util.HashMap;

public class Span implements Serializable{

    private Span parentSpan = null;
    private static SpanSaver saver;

    static {
        saver = SpanSaver.fromString(getSaverType());
    }

    // The method jsonEscaped is based on
    // https://github.com/openzipkin/zipkin/blob/master/zipkin/src/main/java/zipkin/internal/Buffer.java
    // which is available under http://www.apache.org/licenses/LICENSE-2.0

    /**
     * From RFC 7159, "All Unicode characters may be placed within the
     * quotation marks except for the characters that must be escaped:
     * quotation mark, reverse solidus, and the control characters
     * (U+0000 through U+001F)."
     *
     * We also escape '\u2028' and '\u2029', which JavaScript interprets as
     * newline characters. This prevents eval() from failing with a syntax
     * error. http://code.google.com/p/google-gson/issues/detail?id=341
     */
    private static final String[] REPLACEMENT_CHARS;

    static {
        REPLACEMENT_CHARS = new String[128];
        for (int i = 0; i <= 0x1f; i++) {
            REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i);
        }
        REPLACEMENT_CHARS['"'] = "\\\"";
        REPLACEMENT_CHARS['\\'] = "\\\\";
        REPLACEMENT_CHARS['\t'] = "\\t";
        REPLACEMENT_CHARS['\b'] = "\\b";
        REPLACEMENT_CHARS['\n'] = "\\n";
        REPLACEMENT_CHARS['\r'] = "\\r";
        REPLACEMENT_CHARS['\f'] = "\\f";
    }
    private static final String U2028 = "\\u2028";
    private static final String U2029 = "\\u2029";


    String jsonEscaped(String v) {
        int afterReplacement = 0;
        int length = v.length();
        StringBuilder builder = null;
        for (int i = 0; i < length; i++) {
            char c = v.charAt(i);
            String replacement;
            if (c < 0x80) {
                replacement = REPLACEMENT_CHARS[c];
                if (replacement == null) continue;
            } else if (c == '\u2028') {
                replacement = U2028;
            } else if (c == '\u2029') {
                replacement = U2029;
            } else {
                continue;
            }
            if (afterReplacement < i) { // write characters between the last replacement and now
                if (builder == null) builder = new StringBuilder();
                builder.append(v, afterReplacement, i);
            }
            if (builder == null) builder = new StringBuilder();
            builder.append(replacement);
            afterReplacement = i + 1;
        }
        if (builder == null) { // then we didn't escape anything
            return v;
        }
        if (afterReplacement < length) {
            builder.append(v, afterReplacement, length);
        }
        return builder.toString();
    }


    private String getStackTrace(Thread thread){
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        // skip first 4 elements since they contain always calls to method getStackTrace an the rest of span related
        // calls
        StackTraceElement[] elements = thread.getStackTrace();
        for(int i = 4; i<elements.length; i++){
            sb.append("\"").append(elements[i].toString()).append("\"");
            sb.append(",");
        }
        // remove tailing delimiter
        sb.setLength(sb.length() - 1);
        sb.append("]");

        return sb.toString();
    }

    public void setStackTrace(Thread thread){
        binaryAnnotations.put("stacktrace", getStackTrace(thread));
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

    public long getTimestamp(){
        return timestamp;
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
                "\"value\":\"" + jsonEscaped(value) + "\",\n" +
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
