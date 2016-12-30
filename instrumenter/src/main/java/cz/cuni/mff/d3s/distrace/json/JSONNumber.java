package cz.cuni.mff.d3s.distrace.json;

/**
 * Class representing JSON number
 */
public class JSONNumber extends JSONValue {
    private final String value;

    public JSONNumber(String value) {
        if (value == null) {
            throw new NullPointerException("String passed to JSON number is null!");
        }
        this.value = value;
    }

    @Override
    void write(JSONStringBuilder writer) {
        writer.appendNumber(value);
    }
}
