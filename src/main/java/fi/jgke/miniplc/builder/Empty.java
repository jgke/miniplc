package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.tokenizer.TokenQueue;

import java.util.ArrayList;

public class Empty implements Rule {

    public static Empty empty() {
        return new Empty();
    }

    @Override
    public boolean matches(TokenQueue tokenQueue) {
        return true;
    }

    @Override
    public ConsumedRule consume(TokenQueue tokenQueue) {
        return new SimpleConsumedRule(new ArrayList<>());
    }

    public String str() {
        return "Empty{}";
    }
}
