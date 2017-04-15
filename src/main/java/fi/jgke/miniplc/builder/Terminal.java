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

package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.tokenizer.TokenQueue;
import fi.jgke.miniplc.tokenizer.TokenValue;

public class Terminal implements Rule {

    private final TokenValue tokenValue;

    private Terminal(TokenValue tokenValue) {
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

    public static final Rule var = new Terminal(TokenValue.VAR);
    public static final Rule identifier = new Terminal(TokenValue.IDENTIFIER);
    public static final Rule colon = new Terminal(TokenValue.COLON);
    public static final Rule type = new Terminal(TokenValue.TYPE);
    public static final Rule assign = new Terminal(TokenValue.ASSIGN);
    public static final Rule not = new Terminal(TokenValue.NOT);
    public static final Rule intConst = new Terminal(TokenValue.INT_CONST);
    public static final Rule stringConst = new Terminal(TokenValue.STRING_CONST);
    public static final Rule boolConst = new Terminal(TokenValue.BOOL_CONST);
    public static final Rule openBrace = new Terminal(TokenValue.OPEN_BRACE);
    public static final Rule closeBrace = new Terminal(TokenValue.CLOSE_BRACE);
    public static final Rule plus = new Terminal(TokenValue.PLUS);
    public static final Rule minus = new Terminal(TokenValue.MINUS);
    public static final Rule times = new Terminal(TokenValue.TIMES);
    public static final Rule divide = new Terminal(TokenValue.DIVIDE);
    public static final Rule lessThan = new Terminal(TokenValue.LESS_THAN);
    public static final Rule equals = new Terminal(TokenValue.EQUALS);
    public static final Rule print = new Terminal(TokenValue.PRINT);
    public static final Rule semicolon = new Terminal(TokenValue.SEMICOLON);
    public static final Rule read = new Terminal(TokenValue.READ);
    public static final Rule Assert = new Terminal(TokenValue.ASSERT);
    public static final Rule For = new Terminal(TokenValue.FOR);
    public static final Rule in = new Terminal(TokenValue.IN);
    public static final Rule range = new Terminal(TokenValue.RANGE);
    public static final Rule Do = new Terminal(TokenValue.DO);
    public static final Rule end = new Terminal(TokenValue.END);
    public static final Rule eos = new Terminal(TokenValue.EOS);
    public static final Rule and = new Terminal(TokenValue.AND);
}
