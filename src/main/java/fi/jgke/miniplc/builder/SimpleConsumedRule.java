package fi.jgke.miniplc.builder;

public class SimpleConsumedRule extends ConsumedRule {
    private final Object content;

    SimpleConsumedRule(Object content) {
        super(null, (ignored, parameters) -> content);
        this.content = content;
    }

    @Override
    public String toString() {
        return "SimpleConsumedRule{" +
                "content=" + (content instanceof Terminal ? (Terminal)content : content) +
                '}';
    }
}
