package cz.cuni.mff.d3s.distrace.json;

import java.io.Serializable;

/**
 * This class represents all JSON types - JSON literal, number, string, object and array
 */
public abstract class JSONValue implements Serializable {

    public String toJSONString(){
        JSONStringBuilder writer = new JSONStringBuilder();
        write(writer);
        return writer.toString();
    }

    abstract void write(JSONStringBuilder writer);

}
