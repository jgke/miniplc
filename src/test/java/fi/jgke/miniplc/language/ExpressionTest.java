/*
 * Copyright 2016 Jaakko Hannikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fi.jgke.miniplc.language;

import fi.jgke.miniplc.exception.RuntimeException;
import fi.jgke.miniplc.exception.UnexpectedCharacterException;
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.InputOutput;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import fi.jgke.miniplc.builder.Builder;
import fi.jgke.miniplc.builder.ConsumedRule;
import fi.jgke.miniplc.tokenizer.Token;
import fi.jgke.miniplc.tokenizer.TokenQueue;
import fi.jgke.miniplc.tokenizer.TokenValue;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpressionTest {


    private TokenQueue with(Token... tokens) throws UnexpectedCharacterException {
        TokenQueue tokenQueue = new TokenQueue("");
        for (Object token : tokens) {
            tokenQueue.add((Token) token);
        }
        return tokenQueue;
    }

    private void testWith(VariableType variableType, Object value, Token... tokens)
            throws RuntimeException, UnexpectedCharacterException {
        Context context = new Context(InputOutput.getInstance());
        ConsumedRule consume = Builder.expression().consume(TokenQueue.of(tokens));

        Variable var = consume.getVariable(context);

        assertEquals(var.getType(), variableType);
        assertEquals(var.getValue(), value);
    }

    @Test
    public void parseNot() throws UnexpectedCharacterException, RuntimeException {
        Token op = new Token(TokenValue.NOT);
        Token arg = new Token(TokenValue.BOOLCONST, true);
        testWith(VariableType.BOOL, false, op, arg);
    }

    private void test(Object left, TokenValue leftType, TokenValue op, Object right, TokenValue rightType,
                      Object expected, VariableType expectedType) throws UnexpectedCharacterException, RuntimeException {
        Token leftToken = new Token(leftType, left);
        Token operator = new Token(op);
        Token rightToken = new Token(rightType, right);
        testWith(expectedType, expected, leftToken, operator, rightToken);
    }

    private void testInteger(int left, TokenValue op, int right, int expected)
            throws UnexpectedCharacterException, RuntimeException {
        test(left, TokenValue.INTCONST, op, right, TokenValue.INTCONST, expected, VariableType.INT);
    }

    @Test
    public void parseIntegerPlus() throws UnexpectedCharacterException, RuntimeException {
        testInteger(5, TokenValue.PLUS, 3, 8);
    }

    @Test
    public void parseIntegerMinus() throws UnexpectedCharacterException, RuntimeException {
        testInteger(5, TokenValue.MINUS, 3, 2);
    }

    @Test
    public void parseIntegerTimes() throws UnexpectedCharacterException, RuntimeException {
        testInteger(5, TokenValue.TIMES, 3, 15);
    }

    @Test
    public void parseIntegerDivide() throws UnexpectedCharacterException, RuntimeException {
        testInteger(5, TokenValue.DIVIDE, 3, 1);
    }

    @Test
    public void parseIntegerLessThan() throws UnexpectedCharacterException, RuntimeException {
        test(5, TokenValue.INTCONST, TokenValue.LESSTHAN, 3, TokenValue.INTCONST, false, VariableType.BOOL);
    }

    @Test
    public void parseIntegerEquals() throws UnexpectedCharacterException, RuntimeException {
        test(5, TokenValue.INTCONST, TokenValue.EQUALS, 3, TokenValue.INTCONST, false, VariableType.BOOL);
    }

    @Test
    public void parseStringPlus() throws UnexpectedCharacterException, RuntimeException {
        test("foo", TokenValue.STRINGCONST, TokenValue.PLUS, "bar", TokenValue.STRINGCONST, "foobar", VariableType.STRING);
    }

    @Test
    public void parseStringEquals() throws UnexpectedCharacterException, RuntimeException {
        test("foo", TokenValue.STRINGCONST, TokenValue.EQUALS, "bar", TokenValue.STRINGCONST, false, VariableType.BOOL);
    }

    @Test
    public void parseBoolAnd() throws UnexpectedCharacterException, RuntimeException {
        test(false, TokenValue.BOOLCONST, TokenValue.AND, true, TokenValue.BOOLCONST, false, VariableType.BOOL);
    }

    @Test
    public void parseBoolEquals() throws UnexpectedCharacterException, RuntimeException {
        test(false, TokenValue.BOOLCONST, TokenValue.EQUALS, true, TokenValue.BOOLCONST, false, VariableType.BOOL);
    }
}