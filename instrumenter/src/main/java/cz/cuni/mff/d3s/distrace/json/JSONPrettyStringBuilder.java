package cz.cuni.mff.d3s.distrace.json;

/**
 * JSON printer which prints the JSON in the human readable format
 */
public class JSONPrettyStringBuilder extends JSONStringBuilder {

    private static final String indentStr = "  ";
    private int indent;

    @Override
    protected JSONStringBuilder appendArrayOpen() {
        indent++;
        super.appendArrayOpen();
        appendNewLine();
        return this;
    }

    @Override
    protected JSONStringBuilder appendArrayClose() {
        indent--;
        appendNewLine();
        super.appendArrayClose();
        return this;
    }

    @Override
    protected JSONStringBuilder appendArraySeparator() {
        super.appendArraySeparator();
        appendNewLine();
        return this;
    }

    @Override
    protected JSONStringBuilder appendObjectOpen() {
        indent++;
        super.appendObjectOpen();
        appendNewLine();
        return this;
    }

    @Override
    protected JSONStringBuilder appendObjectClose() {
        indent--;
        appendNewLine();
        super.appendObjectClose();
        return this;
    }

    @Override
    protected JSONStringBuilder appendMemberSeparator() {
        super.appendMemberSeparator();
        sb.append(' ');
        return this;
    }

    @Override
    protected JSONStringBuilder appendObjectSeparator() {
        super.appendObjectSeparator();
        appendNewLine();
        return this;
    }

    @Override
    protected JSONStringBuilder removeSingleTrailingChar() {
        super.removeSingleTrailingChar(); // for separator
        super.removeSingleTrailingChar(); // for new line character
        return this;
    }

    private boolean appendNewLine() {
        sb.append("\n");
        for (int i = 0; i < indent; i++) {
            sb.append(indentStr);
        }
        return true;
    }


}
