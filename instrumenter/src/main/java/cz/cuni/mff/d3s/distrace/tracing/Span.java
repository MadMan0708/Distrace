package cz.cuni.mff.d3s.distrace.tracing;


import cz.cuni.mff.d3s.distrace.json.JSONArray;
import cz.cuni.mff.d3s.distrace.json.JSONObject;
import cz.cuni.mff.d3s.distrace.json.JSONValue;
import cz.cuni.mff.d3s.distrace.storage.SpanSaver;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Span class is used to encapsulate a relatively small part of the communication between the nodes.
 * It can be used to track the communication on the same nodes between different threads or
 * on different nodes via the network.
 * Spans can be open and closed. {@link TraceContext} provides public api for closing and opening the spans.
 */
public class Span implements Serializable {

    private static SpanSaver saver;

    static {
        saver = SpanSaver.fromString(getSaverType());
    }

    private Span parentSpan = null;
    private HashMap<String, String> binaryAnnotations = new HashMap<>();
    private HashMap<String, Long> annotations = new HashMap<>();
    private String traceId;
    private String spanId;
    private long timestamp;
    private long duration;
    private String serviceName = "Unknown";
    private String name = "Unknown";
    private int port = 0;
    private String ipv4;
    private JSONObject cachedEndpoint = null;

    /**
     * Internal constructor used to create a new top-level span.
     * Top level span is a span without the parent span.
     *
     * @param traceId current trace id
     * @param name    span name
     */
    Span(String traceId, String name) {
        this.traceId = traceId;
        this.parentSpan = null;
        this.name = name;
        this.timestamp = System.currentTimeMillis() * 1000;
        this.spanId = Long.toHexString(new Random().nextLong());
        addOriginStartAnn(timestamp);
    }

    /**
     * Internal constructor used to create a new nested span. Nested span is a span with a parent span.
     *
     * @param traceId    current trace id
     * @param parentSpan parent span
     * @param name       span name
     */
    Span(String traceId, Span parentSpan, String name) {
        this.traceId = traceId;
        this.spanId = Long.toHexString(new Random().nextLong());
        this.timestamp = System.currentTimeMillis() * 1000;
        this.parentSpan = parentSpan;
        this.name = name;
        addOriginStartAnn(timestamp);
    }

    /**
     * Internal constructor used to create a copy of span based on the existing span
     *
     * @param span current span
     */
    Span(Span span) {
        this.traceId = span.traceId;
        this.spanId = span.spanId;
        this.timestamp = span.timestamp;
        // it is not required to copy this field as the nested spans should not
        // go above the level of thi span
        this.parentSpan = span.parentSpan == null ? null : new Span(span.parentSpan);
        this.name = span.name;
        this.annotations = getAnnotationsCopy(span.annotations);
        this.binaryAnnotations = getAnnotationsCopy(span.binaryAnnotations);
        this.ipv4 = span.ipv4;
        this.port = span.port;
    }

    /**
     * Set stack trace in the moment of the creation of the span as the annotation
     *
     * @param thread thread from which to get stack trace
     */
    public void setOpenStackTrace(Thread thread) {
        setStackTrace("opening", thread);
    }

    /**
     * Set stack trace in the moment of the closing of the span as the annotation
     *
     * @param thread thread from which to get stack trace
     */
    public void setCloseStackTrace(Thread thread) {
        setStackTrace("closing", thread);
    }

    /**
     * Set service name of this span. This name can be used to filter different spans in the Zipkin UI
     * ( When Zipkin UI is used )
     *
     * @param serviceName service name
     */
    public Span setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public Span setIpPort(String ipPort) {
        final String[] split = ipPort.split(":");
        ipv4 = split[0];
        port = Integer.parseInt(split[1]);
        return this;
    }

    public Span setIp(String ip) {
        ipv4 = ip;
        return this;
    }

    public Span setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * Append string to the span name
     *
     * @param append string to append
     * @return span with edited name
     */
    public Span appendToName(String append) {
        this.name = this.name + append;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getName() {
        return name;
    }

    public Span setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Save this span using the set span saver.
     */
    void save() {
        long time = System.currentTimeMillis() * 1000;
        addOriginReceivedAnn(time);
        duration = time - timestamp;
        if (duration < 1) {
            // this prevents the UI from showing NaNs
            // when duration is < 1 we assign it 1 microsecond ( the smallest unit possible in this
            // tool )
            duration = 1;
        }
        saver.saveSpan(this);
    }

    public String getParentSpanId() {
        if (parentSpan == null) {
            // parent span ID = 0 means no parent span ID
            return null;
        } else {
            return parentSpan.getSpanId();
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Span getParentSpan() {
        return parentSpan;
    }

    public String getSpanId() {
        return spanId;
    }

    /**
     * Get value of annotation of type String
     *
     * @param key key of the annotation
     */
    public String getStringValue(String key) {
        return binaryAnnotations.get(key);
    }

    /**
     * Get value of annotation of type Double
     *
     * @param key key of the annotation
     */
    public Double getDoubleValue(String key) {
        try {
            if (binaryAnnotations.get(key) == null) {
                return null;
            } else {
                return Double.parseDouble(binaryAnnotations.get(key));
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("It is expected that the key is of type Double!");
        }
    }

    /**
     * Get value of annotation of type Long
     *
     * @param key key of the annotation
     */
    public Long getLongValue(String key) {
        try {
            if (binaryAnnotations.get(key) == null) {
                return null;
            } else {
                return Long.parseLong(binaryAnnotations.get(key));
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("It is expected that the key is of type Long!");
        }
    }

    /**
     * Check whether the annotation with the specified key is defined on the span
     *
     * @param key key
     */
    public boolean hasAnnotation(String key) {
        return binaryAnnotations.containsKey(key);
    }

    public Span add(String key, String value) {
        binaryAnnotations.put(key, value);
        return this;
    }

    public Span add(String key, boolean value) {
        binaryAnnotations.put(key, value ? "true" : "false");
        return this;
    }

    public Span add(String key, long value) {
        binaryAnnotations.put(key, value + "");
        return this;
    }


    public Span add(String key, int value) {
        binaryAnnotations.put(key, value + "");
        return this;
    }

    /**
     * Return current span as JSON
     *
     * @return JSON representation of the current Span
     */
    public JSONValue toJSON() {
        JSONObject jsonSpan = new JSONObject()
                .add("traceId", traceId)
                .add("name", name)
                .add("id", spanId)
                .addIfNotNull("parentId", getParentSpanId())
                .add("timestamp", timestamp)
                .add("duration", duration)
                .addIfNotEmpty("binaryAnnotations", getBinaryAnnotationsJSON())
                .addIfNotEmpty("annotations", getAnnotationsJSON());

        return new JSONArray(jsonSpan);
    }

    /**
     * Create top-level span
     */
    static Span newTopSpan(String traceId, String name) {
        return new Span(traceId, name);

    }

    /**
     * Create nested span
     */
    static Span newNestedSpan(String traceId, Span parentSpan, String name) {
        return new Span(traceId, parentSpan, name);
    }

    private void setStackTrace(String type, Thread thread) {
        binaryAnnotations.put(type + " stacktrace", getStackTraceAsJSON(thread).toString());
    }

    /**
     * Get stack trace as JSON Array from the provided thread
     *
     * @param thread thread from which to get stack trace
     */
    private JSONArray getStackTraceAsJSON(Thread thread) {
        JSONArray stackTrace = new JSONArray();
        // skip first 4 elements since they contain always calls to method getStackTraceAsJSON an the rest of span related
        // calls
        StackTraceElement[] elements = thread.getStackTrace();
        for (int i = 4; i < elements.length; i++) {
            stackTrace.add(elements[i].toString());
        }
        return stackTrace;
    }


    /**
     * Add Zipkin special annotation denoting client send event
     *
     * @param timestamp time stamp of the event
     * @return current span
     */
    private Span addOriginStartAnn(long timestamp) {
        annotations.put("cs", timestamp);
        return this;
    }

    /**
     * Add Zipkin special annotation denoting server receive event
     *
     * @param timestamp time stamp of the event
     * @return current span
     */
    private Span addTargetReceivedAnn(long timestamp) {
        annotations.put("sr", timestamp);
        return this;
    }

    /**
     * Add Zipkin special annotation denoting client server send
     *
     * @param timestamp time stamp of the event
     * @return current span
     */
    private Span addTargetSentAnn(long timestamp) {
        annotations.put("ss", timestamp);
        return this;
    }

    /**
     * Add Zipkin special annotation denoting client receive event
     *
     * @param timestamp time stamp of the event
     * @return current span
     */
    private Span addOriginReceivedAnn(long timestamp) {
        annotations.put("cr", timestamp);
        return this;
    }

    private JSONObject getEndpointJSON() {
        if (cachedEndpoint == null) {
            cachedEndpoint = new JSONObject()
                    .add("serviceName", serviceName);
            if (port != 0) {
                cachedEndpoint.add("port", port);
            }
            if (ipv4 != null) {
                cachedEndpoint.add("ipv4", ipv4);
            }
        }
        return cachedEndpoint;
    }

    private JSONObject getAnnotationJSON(String key, long value) {
        return new JSONObject()
                .add("value", key)
                .add("timestamp", value)
                .add("endpoint", getEndpointJSON());
    }

    private JSONObject getBinaryAnnotationJSON(String key, String value) {
        return new JSONObject()
                .add("key", key)
                .add("value", value)
                .add("endpoint", getEndpointJSON());
    }

    private JSONArray getBinaryAnnotationsJSON() {
        JSONArray binAnn = new JSONArray();
        for (String name : binaryAnnotations.keySet()) {
            binAnn.add(getBinaryAnnotationJSON(name, binaryAnnotations.get(name)));
        }
        return binAnn;
    }

    private JSONArray getAnnotationsJSON() {
        JSONArray ann = new JSONArray();
        for (String name : annotations.keySet()) {
            ann.add(getAnnotationJSON(name, annotations.get(name)));
        }
        return ann;
    }

    /**
     * Get span saver type provided by the user or the default one if not set
     */
    private static native String getSaverType();

    /**
     * Create deep copy of annotation hash map
     *
     * @param annotations annotation hash map
     * @param <K>         key type
     * @param <V>         value type
     * @return copy
     */
    private static <K, V> HashMap<K, V> getAnnotationsCopy(HashMap<K, V> annotations) {
        HashMap<K, V> map = new HashMap<>();
        for (Map.Entry<K, V> entry : annotations.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

}
