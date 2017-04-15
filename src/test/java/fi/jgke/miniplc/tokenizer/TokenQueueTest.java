/*
 * Copyright 2017 Jaakko Hannikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fi.jgke.miniplc.tokenizer;

import fi.jgke.miniplc.exception.EndOfInputException;
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
        tokens.put("<", TokenValue.LESS_THAN);
        tokens.put("=", TokenValue.EQUALS);
        tokens.put(":=", TokenValue.ASSIGN);
        tokens.put("/", TokenValue.DIVIDE);
        tokens.put("/* foo */ +", TokenValue.PLUS);
        tokens.put("/*\nfoo\n*/ +", TokenValue.PLUS);
        tokens.put("/*\nfoo\n*\n*/ +", TokenValue.PLUS);
        tokens.put("// foo bar\n-", TokenValue.MINUS);
        tokens.put("foo", TokenValue.IDENTIFIER);
        tokens.put("100", TokenValue.INT_CONST);
        tokens.put("\"foo\nbar\"", TokenValue.STRING_CONST);
        tokens.put("true", TokenValue.BOOL_CONST);
        tokens.put("false", TokenValue.BOOL_CONST);
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

    @Test(expected = EndOfInputException.class)
    public void testEndOfComment() {
        new TokenQueue("/*");
    }

    @Test(expected = EndOfInputException.class)
    public void testEndOfString() {
        new TokenQueue("\"");
    }

    @Test(expected = UnexpectedCharacterException.class)
    public void testUnexpectedUnderscore() {
        new TokenQueue("_");
    }

    @Test(expected = UnexpectedCharacterException.class)
    public void testBadNumber() {
        new TokenQueue("5a");
    }
}