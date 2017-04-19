package cz.cuni.mff.d3s.distrace.json;


import java.util.ArrayList;
import java.util.List;

public class JSONArray extends JSONValue {
    private final List<JSONValue> values;

    public JSONArray(JSONObject... values){
        this();
        for(JSONObject value : values){
            add(value);
        }
    }

    public JSONArray() {
        values = new ArrayList<>();
    }

    public int size(){
        return values.size();
    }

    public JSONArray add(int value){
        values.add(JSON.valueOf(value));
        return this;
    }

    public JSONArray add(long value){
        values.add(JSON.valueOf(value));
        return this;
    }

    public JSONArray add(float value){
        values.add(JSON.valueOf(value));
        return this;
    }

    public JSONArray add(double value){
        values.add(JSON.valueOf(value));
        return this;
    }

    public JSONArray add(boolean value){
        values.add(JSON.valueOf(value));
        return this;
    }

    public JSONArray add(String value){
        values.add(JSON.valueOf(value));
        return this;
    }

    public JSONArray add(JSONValue value){
        values.add(value);
        return this;
    }

    @Override
    void write(JSONStringBuilder writer) {
        writer.appendArrayOpen();
        for (JSONValue value : values) {
            value.write(writer);
            writer.appendArraySeparator();
        }
        writer.removeSingleTrailingChar();
        writer.appendArrayClose();
    }
}
