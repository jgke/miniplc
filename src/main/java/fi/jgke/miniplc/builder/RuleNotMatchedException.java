package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.exception.RuntimeException;

public class RuleNotMatchedException extends RuntimeException {
    public RuleNotMatchedException(int linenumber) {
        super("Failed to parse file near line " + linenumber);
    }
}
