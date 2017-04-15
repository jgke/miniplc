package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.tokenizer.TokenQueue;

public interface Rule {
    boolean matches(TokenQueue tokenQueue);
    ConsumedRule consume(TokenQueue tokenQueue);
    default String str() {
        return "default rule";
    }
}
