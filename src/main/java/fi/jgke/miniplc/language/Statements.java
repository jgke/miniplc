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

import fi.jgke.miniplc.TokenValue;
import fi.jgke.miniplc.interpreter.RuntimeException;
import fi.jgke.miniplc.interpreter.Stack;
import fi.jgke.miniplc.interpreter.TokenQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Statements implements Executable {
    private List<Statement> statements;

    public Statements() {
        statements = new ArrayList<>();
    }

    // <stmts>  ::=  <stmt> ";" ( <stmt> ";" )*
    @Override
    public void parse(TokenQueue tokens) throws RuntimeException {
        /* Peek out END so that parsing stops at block scope */
        while(!tokens.isEmpty() && !tokens.peek().getValue().equals(TokenValue.END)) {
            Statement statement = new Statement();
            statement.parse(tokens);
            statements.add(statement);

            tokens.getExpectedToken(TokenValue.SEMICOLON);
        }
    }

    @Override
    public void execute(Stack stack) throws RuntimeException {
        for (Statement statement : statements)
            statement.execute(stack);
    }
}
