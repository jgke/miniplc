package fi.jgke.miniplc.exception;

public class EndOfInputException extends RuntimeException {
    public EndOfInputException() {
        super("Unexpected end of input");
    }
}
