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

public class Terminal extends Rule {

    private final TokenValue tokenValue;

    private Terminal(TokenValue tokenValue) {
        this.tokenValue = tokenValue;
    }

    @Override
    public boolean matches() {
        return tokenQueue
                .element()
                .getValue()
                .equals(tokenValue);
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "tokenValue=" + tokenValue +
                '}';
    }

    @Override
    public ConsumedRule consume() {
        return new SimpleConsumedRule(tokenQueue.getExpectedToken(tokenValue));
    }

    public static final Rule Var = new Terminal(TokenValue.VAR);
    public static final Rule Identifier = new Terminal(TokenValue.IDENTIFIER);
    public static final Rule Colon = new Terminal(TokenValue.COLON);
    public static final Rule Type = new Terminal(TokenValue.TYPE);
    public static final Rule Assign = new Terminal(TokenValue.ASSIGN);
    public static final Rule Not = new Terminal(TokenValue.NOT);
    public static final Rule IntConst = new Terminal(TokenValue.INT_CONST);
    public static final Rule StringConst = new Terminal(TokenValue.STRING_CONST);
    public static final Rule BoolConst = new Terminal(TokenValue.BOOL_CONST);
    public static final Rule OpenBrace = new Terminal(TokenValue.OPEN_BRACE);
    public static final Rule CloseBrace = new Terminal(TokenValue.CLOSE_BRACE);
    public static final Rule Plus = new Terminal(TokenValue.PLUS);
    public static final Rule Minus = new Terminal(TokenValue.MINUS);
    public static final Rule Times = new Terminal(TokenValue.TIMES);
    public static final Rule Divide = new Terminal(TokenValue.DIVIDE);
    public static final Rule LessThan = new Terminal(TokenValue.LESS_THAN);
    public static final Rule Equals = new Terminal(TokenValue.EQUALS);
    public static final Rule Print = new Terminal(TokenValue.PRINT);
    public static final Rule Semicolon = new Terminal(TokenValue.SEMICOLON);
    public static final Rule Read = new Terminal(TokenValue.READ);
    public static final Rule Assert = new Terminal(TokenValue.ASSERT);
    public static final Rule For = new Terminal(TokenValue.FOR);
    public static final Rule In = new Terminal(TokenValue.IN);
    public static final Rule Range = new Terminal(TokenValue.RANGE);
    public static final Rule Do = new Terminal(TokenValue.DO);
    public static final Rule End = new Terminal(TokenValue.END);
    public static final Rule Eos = new Terminal(TokenValue.EOS);
    public static final Rule And = new Terminal(TokenValue.AND);
}
