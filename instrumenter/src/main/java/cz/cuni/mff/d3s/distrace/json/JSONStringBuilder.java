package cz.cuni.mff.d3s.distrace.json;

/**
 * Simple wrapper around java String Builder used to build JSON strings
 */
public class JSONStringBuilder {

    private final StringBuilder sb;

    JSONStringBuilder() {
        sb = new StringBuilder();
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    JSONStringBuilder appendLiteral(String value) {
        sb.append(value);
        return this;
    }

    JSONStringBuilder appendNumber(String value) {
        sb.append(value);
        return this;
    }

    JSONStringBuilder appendString(String value) {
        sb.append('"').append(jsonEscaped(value)).append('"');
        return this;
    }

    JSONStringBuilder appendArrayOpen() {
        sb.append('[');
        return this;
    }

    JSONStringBuilder appendArrayClose() {
        sb.append(']');
        return this;
    }

    JSONStringBuilder appendArraySeparator() {
        sb.append(',');
        return this;
    }

    JSONStringBuilder removeTrailingSeparator(){
        sb.setLength(sb.length() - 1);
        return this;
    }
    JSONStringBuilder appendObjectOpen() {
        sb.append('{');
        return this;
    }

    JSONStringBuilder appendObjectClose() {
        sb.append('}');
        return this;
    }

    JSONStringBuilder appendMemberName(String name) {
       return appendString(name);
    }

    JSONStringBuilder appendMemberSeparator()  {
        sb.append(':');
        return this;
    }

    JSONStringBuilder appendObjectSeparator() {
        sb.append(',');
        return this;
    }


    // The method jsonEscaped and related fields is based on
    // https://github.com/openzipkin/zipkin/blob/master/zipkin/src/main/java/zipkin/internal/Buffer.java
    // which is available under http://www.apache.org/licenses/LICENSE-2.0

    /**
     * From RFC 7159, "All Unicode characters may be placed within the
     * quotation marks except for the characters that must be escaped:
     * quotation mark, reverse solidus, and the control characters
     * (U+0000 through U+001F)."
     *
     * We also escape '\u2028' and '\u2029', which JavaScript interprets as
     * newline characters. This prevents eval() from failing with a syntax
     * error. http://code.google.com/p/google-gson/issues/detail?id=341
     */
    private static final String[] REPLACEMENT_CHARS;

    static {
        REPLACEMENT_CHARS = new String[128];
        for (int i = 0; i <= 0x1f; i++) {
            REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i);
        }
        REPLACEMENT_CHARS['"'] = "\\\"";
        REPLACEMENT_CHARS['\\'] = "\\\\";
        REPLACEMENT_CHARS['\t'] = "\\t";
        REPLACEMENT_CHARS['\b'] = "\\b";
        REPLACEMENT_CHARS['\n'] = "\\n";
        REPLACEMENT_CHARS['\r'] = "\\r";
        REPLACEMENT_CHARS['\f'] = "\\f";
    }
    private static final String U2028 = "\\u2028";
    private static final String U2029 = "\\u2029";

    /**
     * Escape string so it can be saved into JSON
     * @param original string to escape
     * @return escaped string
     */
    private String jsonEscaped(String original) {
        int afterReplacement = 0;
        int length = original.length();
        StringBuilder builder = null;
        for (int i = 0; i < length; i++) {
            char c = original.charAt(i);
            String replacement;
            if (c < 0x80) {
                replacement = REPLACEMENT_CHARS[c];
                if (replacement == null) continue;
            } else if (c == '\u2028') {
                replacement = U2028;
            } else if (c == '\u2029') {
                replacement = U2029;
            } else {
                continue;
            }
            if (afterReplacement < i) { // write characters between the last replacement and now
                if (builder == null) builder = new StringBuilder();
                builder.append(original, afterReplacement, i);
            }
            if (builder == null) builder = new StringBuilder();
            builder.append(replacement);
            afterReplacement = i + 1;
        }
        if (builder == null) { // then we didn't escape anything
            return original;
        }
        if (afterReplacement < length) {
            builder.append(original, afterReplacement, length);
        }
        return builder.toString();
    }


}
