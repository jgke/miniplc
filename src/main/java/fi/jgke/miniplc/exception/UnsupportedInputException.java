package fi.jgke.miniplc.exception;

public class UnsupportedInputException extends RuntimeException {
    public UnsupportedInputException(int linenumber) {
        super("Unsupported input type near line " + linenumber + ": read can only be used to input variables of type INT and STRING");
    }
}
