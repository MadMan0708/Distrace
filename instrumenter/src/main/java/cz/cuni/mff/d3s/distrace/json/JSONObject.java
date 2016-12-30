package cz.cuni.mff.d3s.distrace.json;

import java.util.*;

/**
 * Class representing JSON object.
 *
 * New elements can be added using methods <code>add</code>
 */
public class JSONObject extends JSONValue {

    private final Map<String, JSONValue> values;
    /**
     * Create empty JSON object
     */
    public JSONObject() {
        values = new HashMap<>();
    }

    public JSONObject add(String name, int value) {
        add(name, JSON.valueOf(value));
        return this;
    }

    public JSONObject add(String name, long value) {
        add(name, JSON.valueOf(value));
        return this;
    }

    public JSONObject add(String name, float value) {
        add(name, JSON.valueOf(value));
        return this;
    }

    public JSONObject add(String name, double value) {
        add(name, JSON.valueOf(value));
        return this;
    }

    public JSONObject add(String name, boolean value) {
        add(name, JSON.valueOf(value));
        return this;
    }

    public JSONObject add(String name, String value) {
        add(name, JSON.valueOf(value));
        return this;
    }

    public JSONObject addIfNotNull(String name, String value){
        if(value != null){
            add(name, value);
        }
        return this;
    }

    public JSONObject addIfNotNull(String name, Integer value){
        if(value != null){
            add(name, value);
        }
        return this;
    }

    public JSONObject addIfNotNull(String name, Long value){
        if(value != null){
            add(name, value);
        }
        return this;
    }

    public JSONObject addIfNotNull(String name, Float value){
        if(value != null){
            add(name, value);
        }
        return this;
    }

    public JSONObject addIfNotNull(String name, Double value){
        if(value != null){
            add(name, value);
        }
        return this;
    }


    public JSONObject add(String name, JSONValue value) {
        values.put(name, value);
        return this;
    }

    @Override
    void write(JSONStringBuilder writer) {
        writer.appendObjectOpen();
        for(Map.Entry<String, JSONValue> e : values.entrySet()){
            writer.appendMemberName(e.getKey());
            writer.appendMemberSeparator();
            e.getValue().write(writer);
            writer.appendObjectSeparator();
        }
        writer.removeTrailingSeparator();
        writer.appendObjectClose();
    }
}
