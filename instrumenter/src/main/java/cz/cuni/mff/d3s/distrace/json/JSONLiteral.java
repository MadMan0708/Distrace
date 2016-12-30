package cz.cuni.mff.d3s.distrace.json;

/**
 * Class representing JSON Literal
 */
public class JSONLiteral extends JSONValue {
    private final String value;


    JSONLiteral(String value) {
        this.value = value;
    }

    @Override
    void write(JSONStringBuilder writer) {
        writer.appendLiteral(value);
    }
}
