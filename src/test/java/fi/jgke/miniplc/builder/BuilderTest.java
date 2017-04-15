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

import fi.jgke.miniplc.exception.OperationNotSupportedException;
import fi.jgke.miniplc.exception.RuntimeException;
import fi.jgke.miniplc.exception.TypeException;
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.InputOutput;
import fi.jgke.miniplc.tokenizer.TokenQueue;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class BuilderTest {

    @Test
    public void parsesStatement() {
        Context context = new Context(InputOutput.getInstance());
        TokenQueue tokenQueue = new TokenQueue("var foo : int := 5 + 5;");

        Builder.parseAndExecute(tokenQueue, context);
    }

    @Test
    public void testExceptions() {
        Map<String, Class<? extends RuntimeException>> samples = new HashMap<>();
        samples.put("print !5;", OperationNotSupportedException.class);
        samples.put("for i in true..5 do print i; end for;", TypeException.class);
        samples.put("for i in 5..true do print i; end for;", TypeException.class);
        samples.put("assert(5);", TypeException.class);
        samples.put("var i : int := (5 + true);", TypeException.class);
        samples.put("var i : int := (true + true);", OperationNotSupportedException.class);
        samples.put("var i : int := (\"foo\" - \"bar\");", OperationNotSupportedException.class);
        InputOutput io = InputOutput.getInstance();

        for(String s : samples.keySet()) {
            try {
                TokenQueue tokenQueue = new TokenQueue(s);
                Context context = new Context(io);
                Builder.parseAndExecute(tokenQueue, context);
                Assert.assertFalse(true);
            } catch(RuntimeException e) {
                Assert.assertEquals(samples.get(s), e.getClass());
            }
        }
    }
}