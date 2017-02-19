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
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.tokenizer.TokenQueue;

public class Program implements Executable {

    private Statements statements;

    public Program() {
        statements = new Statements();
    }

    // <prog>   ::=  <stmts>
    @Override
    public void parse(TokenQueue tokens) throws RuntimeException {
        statements.parse(tokens);
    }

    @Override
    public void execute(Context context) throws RuntimeException {
        statements.execute(context);
    }
}
