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

    public static Rule var = new Terminal(TokenValue.VAR);
    public static Rule varIdent = new Terminal(TokenValue.IDENTIFIER);
    public static Rule colon = new Terminal(TokenValue.COLON);
    public static Rule type = new Terminal(TokenValue.TYPE);
    public static Rule assign = new Terminal(TokenValue.ASSIGN);
    public static Rule not = new Terminal(TokenValue.NOT);
    public static Rule intconst = new Terminal(TokenValue.INTCONST);
    public static Rule stringconst = new Terminal(TokenValue.STRINGCONST);
    public static Rule boolconst = new Terminal(TokenValue.BOOLCONST);
    public static Rule openbrace = new Terminal(TokenValue.OPEN_BRACE);
    public static Rule closebrace = new Terminal(TokenValue.CLOSE_BRACE);
    public static Rule plus = new Terminal(TokenValue.PLUS);
    public static Rule minus = new Terminal(TokenValue.MINUS);
    public static Rule times = new Terminal(TokenValue.TIMES);
    public static Rule divide = new Terminal(TokenValue.DIVIDE);
    public static Rule lessthan = new Terminal(TokenValue.LESSTHAN);
    public static Rule equals = new Terminal(TokenValue.EQUALS);
    public static Rule print = new Terminal(TokenValue.PRINT);
    public static Rule semicolon = new Terminal(TokenValue.SEMICOLON);
    public static Rule read = new Terminal(TokenValue.READ);
    public static Rule Assert = new Terminal(TokenValue.ASSERT);
    public static Rule For = new Terminal(TokenValue.FOR);
    public static Rule in = new Terminal(TokenValue.IN);
    public static Rule range = new Terminal(TokenValue.RANGE);
    public static Rule Do = new Terminal(TokenValue.DO);
    public static Rule end = new Terminal(TokenValue.END);
    public static Rule eos = new Terminal(TokenValue.EOS);
}
