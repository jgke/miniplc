package fi.jgke.miniplc.exception;

public class IntegerParseError extends RuntimeException {
    public IntegerParseError(int linenumber) {
        super("Integer parse error near line " + linenumber);
    }
}
