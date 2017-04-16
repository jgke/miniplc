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
package fi.jgke.miniplc.language;

import fi.jgke.miniplc.builder.Syntax;
import fi.jgke.miniplc.exception.RuntimeException;
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.InputOutput;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import fi.jgke.miniplc.builder.ConsumedRule;
import fi.jgke.miniplc.tokenizer.Token;
import fi.jgke.miniplc.tokenizer.TokenQueue;
import fi.jgke.miniplc.tokenizer.TokenValue;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpressionTest {


    private void testWith(VariableType variableType, Object value, Token... tokens)
            throws RuntimeException {
        Context context = new Context(InputOutput.getInstance());
        ConsumedRule consume = Syntax
                .expression()
                .with(TokenQueue.of(tokens))
                .consume();

        Variable var = consume.getVariable(context);

        assertEquals(var.getType(), variableType);
        assertEquals(var.getValue(), value);
    }

    @Test
    public void parseNot() throws RuntimeException {
        Token op = new Token(TokenValue.NOT);
        Token arg = new Token(TokenValue.BOOL_CONST, true);
        testWith(VariableType.BOOL, false, op, arg);
    }

    @Test
    public void parseNot2() throws RuntimeException {
        Token op = new Token(TokenValue.NOT);
        Token arg = new Token(TokenValue.BOOL_CONST, false);
        testWith(VariableType.BOOL, true, op, arg);
    }

    private void test(Object left, TokenValue leftType, TokenValue op, Object right, TokenValue rightType,
                      Object expected, VariableType expectedType) throws RuntimeException {
        Token leftToken = new Token(leftType, left);
        Token operator = new Token(op);
        Token rightToken = new Token(rightType, right);
        testWith(expectedType, expected, leftToken, operator, rightToken);
    }

    private void testInteger(int left, TokenValue op, int right, int expected)
            throws RuntimeException {
        test(left, TokenValue.INT_CONST, op, right, TokenValue.INT_CONST, expected, VariableType.INT);
    }

    @Test
    public void parseIntegerPlus() throws RuntimeException {
        testInteger(5, TokenValue.PLUS, 3, 8);
    }

    @Test
    public void parseIntegerMinus() throws RuntimeException {
        testInteger(3, TokenValue.MINUS, 2, 1);
    }

    @Test
    public void parseIntegerTimes() throws RuntimeException {
        testInteger(5, TokenValue.TIMES, 4, 20);
    }

    @Test
    public void parseIntegerDivide() throws RuntimeException {
        testInteger(7, TokenValue.DIVIDE, 3, 2);
    }

    @Test
    public void parseIntegerLessThan() throws RuntimeException {
        test(5, TokenValue.INT_CONST, TokenValue.LESS_THAN, 3, TokenValue.INT_CONST, false, VariableType.BOOL);
    }

    @Test
    public void parseIntegerLessThan2() throws RuntimeException {
        test(2, TokenValue.INT_CONST, TokenValue.LESS_THAN, 3, TokenValue.INT_CONST, true, VariableType.BOOL);
    }

    @Test
    public void parseIntegerEquals() throws RuntimeException {
        test(5, TokenValue.INT_CONST, TokenValue.EQUALS, 3, TokenValue.INT_CONST, false, VariableType.BOOL);
    }

    @Test
    public void parseStringPlus() throws RuntimeException {
        test("foo", TokenValue.STRING_CONST, TokenValue.PLUS, "bar", TokenValue.STRING_CONST, "foobar", VariableType.STRING);
    }

    @Test
    public void parseStringEquals() throws RuntimeException {
        test("foo", TokenValue.STRING_CONST, TokenValue.EQUALS, "bar", TokenValue.STRING_CONST, false, VariableType.BOOL);
    }

    @Test
    public void parseBoolAnd() throws RuntimeException {
        test(false, TokenValue.BOOL_CONST, TokenValue.AND, false, TokenValue.BOOL_CONST, false, VariableType.BOOL);
        test(false, TokenValue.BOOL_CONST, TokenValue.AND, true, TokenValue.BOOL_CONST, false, VariableType.BOOL);
        test(true, TokenValue.BOOL_CONST, TokenValue.AND, false, TokenValue.BOOL_CONST, false, VariableType.BOOL);
        test(true, TokenValue.BOOL_CONST, TokenValue.AND, true, TokenValue.BOOL_CONST, true, VariableType.BOOL);
    }

    @Test
    public void parseBoolEquals() throws RuntimeException {
        test(false, TokenValue.BOOL_CONST, TokenValue.EQUALS, true, TokenValue.BOOL_CONST, false, VariableType.BOOL);
    }
}