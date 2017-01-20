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

import fi.jgke.miniplc.Executable;
import fi.jgke.miniplc.Token;
import fi.jgke.miniplc.TokenValue;
import fi.jgke.miniplc.interpreter.*;
import fi.jgke.miniplc.interpreter.RuntimeException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Statement implements Executable {

    public void addVariable(TokenQueue tokens, Stack stack) throws RuntimeException {
        Token identifier = tokens.getExpectedToken(TokenValue.IDENTIFIER);
        tokens.getExpectedToken(TokenValue.COLON);
        Token type = tokens.getExpectedToken(TokenValue.TYPE);
        Variable variable = null;
        if(tokens.peek().getValue() == TokenValue.ASSIGN) {
            tokens.getExpectedToken(TokenValue.ASSIGN);
            Token value;
            switch(type.getVariableType()) {
                case INT:
                    value = tokens.getExpectedToken(TokenValue.INTVAR);
                    variable = new Variable(VariableType.INT, Integer.parseInt(value.getString()));
                    break;
                case STRING:
                    value = tokens.getExpectedToken(TokenValue.STRINGVAR);
                    variable = new Variable(VariableType.STRING, value.getString());
                    break;
                case BOOL:
                    value = tokens.getExpectedToken(TokenValue.BOOLVAR);
                    variable = new Variable(VariableType.BOOL, Boolean.parseBoolean(value.getString()));
                    break;
                default:
                    throw new NotImplementedException();
            }
        }
        stack.addVariable(identifier.getString(), variable);
    }

    @Override
    public void execute(TokenQueue tokens, Stack stack) throws RuntimeException {
        Token token = tokens.remove();
        switch(token.getValue()) {
            case VAR:
                addVariable(tokens, stack);
                break;
        }
    }
}
