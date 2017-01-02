package cz.cuni.mff.d3s.distrace.json;

/**
 * Helper class used to created valid JSON primitives based on Java primitives
 */
public final class JSON {

    /**
     * Represents the JSON literal <code>null</code>.
     */
    public static final JSONValue NULL = new JSONLiteral("null");

    /**
     * Represents the JSON literal <code>true</code>.
     */
    public static final JSONValue TRUE = new JSONLiteral("true");

    /**
     * Represents the JSON literal <code>false</code>.
     */
    public static final JSONValue FALSE = new JSONLiteral("false");

    public static JSONValue valueOf(int value){
        return new JSONNumber(Integer.toString(value, 10));
    }

    public static JSONValue valueOfOrNull(Integer value){
        return value == null ? NULL : valueOf(value);
    }

    static JSONValue valueOf(long value){
        return new JSONNumber(Long.toString(value, 10));
    }

    static JSONValue valueOf(float value){
        if (Float.isInfinite(value) || Float.isNaN(value)) {
            throw new IllegalArgumentException("Infinite and NaN values not permitted in JSON");
        }
        return new JSONNumber(Float.toString(value));
    }

    static JSONValue valueOf(double value){
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new IllegalArgumentException("Infinite and NaN values not permitted in JSON");
        }
        return new JSONNumber(Double.toString(value));
    }

    public static JSONValue valueOf(String value) {
        return value == null ? NULL : new JSONString(value);
    }

    static JSONValue valueOf(boolean value){
        return value ? TRUE : FALSE;
    }
}
