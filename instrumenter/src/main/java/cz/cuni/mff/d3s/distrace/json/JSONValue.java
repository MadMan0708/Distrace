package cz.cuni.mff.d3s.distrace.json;

/**
 * This class represents all JSON types - JSON literal, number, string, object and array
 */
public abstract class JSONValue {

    public String toJSONString(){
        JSONStringBuilder writer = new JSONStringBuilder();
        write(writer);
        return writer.toString();
    }

    abstract void write(JSONStringBuilder writer);

}
