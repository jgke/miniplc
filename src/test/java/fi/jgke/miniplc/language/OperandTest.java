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
import fi.jgke.miniplc.tokenizer.Token;
import fi.jgke.miniplc.tokenizer.TokenQueue;
import fi.jgke.miniplc.tokenizer.TokenValue;
import org.junit.Test;

import static org.junit.Assert.*;

public class OperandTest {

    private TokenQueue with(Token... tokens) throws UnexpectedCharacterException {
        TokenQueue tokenQueue = new TokenQueue("");
        for (Object token : tokens) {
            tokenQueue.add((Token) token);
        }
        return tokenQueue;
    }

    private void testWith(Context context, VariableType variableType, Object value, Token... tokens)
            throws RuntimeException, UnexpectedCharacterException {
        Operand operand = new Operand();
        operand.parse(with(tokens));

        Variable var = operand.execute(context);

        assertEquals(var.getType(), variableType);
        assertEquals(var.getValue(), value);
    }

    private void testWith(TokenValue type, VariableType variableType, Object value)
            throws RuntimeException, UnexpectedCharacterException {
        Context context = new Context(InputOutput.getInstance());
        testWith(context, variableType, value, new Token(type, value));
    }

    @Test
    public void parseInt() throws UnexpectedCharacterException, RuntimeException {
        testWith(TokenValue.INTVAR, VariableType.INT, 5);
    }

    @Test
    public void parseString() throws UnexpectedCharacterException, RuntimeException {
        testWith(TokenValue.STRINGVAR, VariableType.STRING, "Foo");
    }

    @Test
    public void parseBoolean() throws UnexpectedCharacterException, RuntimeException {
        testWith(TokenValue.BOOLVAR, VariableType.BOOL, true);
    }

    @Test
    public void parseIdentifier() throws UnexpectedCharacterException, RuntimeException {
        Context context = new Context(InputOutput.getInstance());
        context.addVariable(new Variable("bar", VariableType.STRING, "foo"));
        testWith(context, VariableType.STRING, "foo", new Token(TokenValue.IDENTIFIER, "bar"));
    }

    @Test
    public void parseStatement() throws UnexpectedCharacterException, RuntimeException {
        Token left = new Token(TokenValue.OPEN_BRACE);
        Token middle = new Token(TokenValue.INTVAR, 5);
        Token right = new Token(TokenValue.CLOSE_BRACE);
        Context context = new Context(InputOutput.getInstance());
        context.addVariable(new Variable("bar", VariableType.STRING, "foo"));
        testWith(context, VariableType.INT, 5, left, middle, right);
    }

}