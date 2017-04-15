package fi.jgke.miniplc.tokenizer;

import fi.jgke.miniplc.exception.UnexpectedCharacterException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TokenQueueTest {
    @Test
    public void testParseValues() throws UnexpectedCharacterException {
        Map<String, TokenValue> tokens = new HashMap<>();
        tokens.put(";", TokenValue.SEMICOLON);
        tokens.put("(", TokenValue.OPEN_BRACE);
        tokens.put(")", TokenValue.CLOSE_BRACE);
        tokens.put("+", TokenValue.PLUS);
        tokens.put("-", TokenValue.MINUS);
        tokens.put("*", TokenValue.TIMES);
        tokens.put("&", TokenValue.AND);
        tokens.put("!", TokenValue.NOT);
        tokens.put("<", TokenValue.LESSTHAN);
        tokens.put("=", TokenValue.EQUALS);
        tokens.put(":=", TokenValue.ASSIGN);
        tokens.put("/", TokenValue.DIVIDE);
        tokens.put("/* foo */ +", TokenValue.PLUS);
        tokens.put("// foo bar\n-", TokenValue.MINUS);
        tokens.put("foo", TokenValue.IDENTIFIER);
        tokens.put("100", TokenValue.INTCONST);
        tokens.put("\"foo\nbar\"", TokenValue.STRINGCONST);
        tokens.put("true", TokenValue.BOOLCONST);
        tokens.put("false", TokenValue.BOOLCONST);
        tokens.put("var", TokenValue.VAR);
        tokens.put("for", TokenValue.FOR);
        tokens.put("end", TokenValue.END);
        tokens.put("in", TokenValue.IN);
        tokens.put("do", TokenValue.DO);
        tokens.put("read", TokenValue.READ);
        tokens.put("print", TokenValue.PRINT);
        tokens.put("int", TokenValue.TYPE);
        tokens.put("string", TokenValue.TYPE);
        tokens.put("bool", TokenValue.TYPE);
        tokens.put("assert", TokenValue.ASSERT);

        for(String key : tokens.keySet()) {
            TokenQueue tokenQueue = new TokenQueue(key);
            assertEquals(tokens.get(key), tokenQueue.remove().getValue());
            assertEquals(TokenValue.EOS, tokenQueue.remove().getValue());
            assertTrue(tokenQueue.isEmpty());
        }
    }
}