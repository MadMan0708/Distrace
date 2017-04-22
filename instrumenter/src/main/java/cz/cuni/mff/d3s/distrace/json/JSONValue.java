package cz.cuni.mff.d3s.distrace.json;

import java.io.Serializable;

/**
 * This class represents all JSON types - JSON literal, number, string, object and array
 */
public abstract class JSONValue implements Serializable {

    @Override
    public String toString() {
        JSONStringBuilder builder = new JSONStringBuilder();
        write(builder);
        return builder.toString();
    }

    public String toString(JSONStringBuilder stringBuilder) {
        write(stringBuilder);
        return stringBuilder.toString();
    }

    abstract void write(JSONStringBuilder writer);

}
