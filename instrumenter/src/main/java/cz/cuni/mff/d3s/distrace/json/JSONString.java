package cz.cuni.mff.d3s.distrace.json;

public class JSONString extends JSONValue {
    private final String value;

    JSONString(String value) {
        if (value == null) {
            throw new NullPointerException("String passed to JSON string is null!");
        }
        this.value = value;
    }

    @Override
    void write(JSONStringBuilder writer) {
        writer.appendString(value);
    }
}
