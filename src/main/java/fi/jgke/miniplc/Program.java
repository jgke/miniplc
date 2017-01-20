package fi.jgke.miniplc;

import fi.jgke.miniplc.interpreter.RuntimeException;
import fi.jgke.miniplc.interpreter.Stack;
import fi.jgke.miniplc.interpreter.TokenQueue;
import fi.jgke.miniplc.language.Statements;

import java.util.ArrayList;

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
public class Program implements Executable {
    
    private Statements statements;

    public Program(Statements statements) {
        this.statements = statements;
    }

    @Override
    public void execute(TokenQueue tokens, Stack stack) throws RuntimeException {
        statements.execute(tokens, stack);
    }
}
