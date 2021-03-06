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
package fi.jgke.miniplc.interpreter;

import fi.jgke.miniplc.exception.RuntimeException;
import fi.jgke.miniplc.builder.Builder;
import fi.jgke.miniplc.tokenizer.TokenQueue;

public class Executor {

    private final String script;

    public Executor(String script) {
        this.script = script;
    }

    public void execute(InputOutput io) throws RuntimeException {
        TokenQueue queue = new TokenQueue(script);
        Builder.parseAndExecute(queue, new Context(io));
    }
}
