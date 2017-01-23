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

import fi.jgke.miniplc.Token;
import fi.jgke.miniplc.TokenValue;
import fi.jgke.miniplc.interpreter.RuntimeException;
import fi.jgke.miniplc.interpreter.*;

import java.util.Optional;

public class Operand implements ExecutableWithResult {

    @FunctionalInterface
    public interface Producer {
        Variable apply(Stack stack) throws RuntimeException;
    }

    Producer producer;

    /* Note: this accepts booleans as well as the specification:
     <opnd>   ::=  <int>
           |   <string>
           |   <var_ident>
           |   "(" expr ")"
     */
    @Override
    public void parse(TokenQueue tokens) throws RuntimeException {
        Token token = tokens.getExpectedToken(TokenValue.INTVAR, TokenValue.STRINGVAR,
                TokenValue.BOOLVAR, TokenValue.IDENTIFIER, TokenValue.OPEN_BRACE);
        switch (token.getValue()) {
            case INTVAR:
                producer = (s) -> new Variable(VariableType.INT, token.getContent());
                break;
            case STRINGVAR:
                producer = (s) -> new Variable(VariableType.STRING, token.getContent());
                break;
            case BOOLVAR:
                producer = (s) -> new Variable(VariableType.BOOL, token.getContent());
                break;
            case IDENTIFIER:
                producer = (s) -> s.getVariable(token.getString());
                break;
            case OPEN_BRACE:
                Expression expression = new Expression();
                expression.parse(tokens);

                producer = expression::execute;

                tokens.getExpectedToken(TokenValue.CLOSE_BRACE);
                break;
        }
    }

    @Override
    public Variable execute(Stack stack) throws RuntimeException {
        return producer.apply(stack);
    }
}
