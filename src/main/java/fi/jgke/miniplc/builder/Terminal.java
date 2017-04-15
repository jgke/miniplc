package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.tokenizer.TokenQueue;
import fi.jgke.miniplc.tokenizer.TokenValue;

public class Terminal implements Rule {

    private TokenValue tokenValue;

    public Terminal(TokenValue tokenValue) {
        this.tokenValue = tokenValue;
    }

    @Override
    public boolean matches(TokenQueue tokenQueue) {
        return tokenQueue.element().getValue().equals(tokenValue);
    }

    @Override
    public ConsumedRule consume(TokenQueue tokenQueue) {
        return new SimpleConsumedRule(tokenQueue.getExpectedToken(tokenValue));
    }

    public String str() {
        return "Terminal{" +
                "tokenValue=" + tokenValue +
                '}';
    }
}
