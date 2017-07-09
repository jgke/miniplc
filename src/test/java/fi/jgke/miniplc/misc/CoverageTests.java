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

package fi.jgke.miniplc.misc;

import fi.jgke.miniplc.Miniplc;
import fi.jgke.miniplc.builder.*;
import fi.jgke.miniplc.builder.handlers.ExpressionHandlers;
import fi.jgke.miniplc.builder.handlers.OperandHandlers;
import fi.jgke.miniplc.builder.handlers.StatementHandlers;
import fi.jgke.miniplc.builder.handlers.StatementsHandlers;
import fi.jgke.miniplc.exception.TypeException;
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.InputOutput;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import fi.jgke.miniplc.tokenizer.Token;
import fi.jgke.miniplc.tokenizer.TokenQueue;
import org.junit.Test;

import static fi.jgke.miniplc.tokenizer.TokenValue.DO;
import static fi.jgke.miniplc.tokenizer.TokenValue.MINUS;
import static fi.jgke.miniplc.tokenizer.TokenValue.SEMICOLON;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/* Tests that purely increase code coverage */
public class CoverageTests {
    @Test
    public void constructorWorks() throws Exception {
        /* Increase code coverage, run dummy constructors */
        new BaseRules();
        new ExpressionHandlers();
        new OperandHandlers();
        new StatementHandlers();
        new StatementsHandlers();
        new Builder();
        new Syntax();
        new Miniplc();
    }

    @Test
    public void simpleConsumedRule() throws Exception {
        assertThat(new SimpleConsumedRule("foo").toString(), containsString("foo"));
    }

    @Test
    public void variable() {
        assertThat(new Variable(VariableType.BOOL, true).toString(), containsString("true"));
    }

    @Test
    public void token() {
        assertThat(new Token(SEMICOLON).toString(), containsString("\n"));
        assertThat(new Token(DO).toString(), containsString("\n"));
        assertThat(new Token(MINUS).toString(), containsString("MINUS"));
    }

    @Test
    public void terminal() throws Exception {
        assertThat(Terminal.And.toString(), containsString("AND"));
    }

    @Test
    public void forLoopVariableLineNumber() throws Exception {
        try {
            String program = "for\ni\nin\n5\n..\ntrue\ndo print i; end for;";
            TokenQueue tokenQueue = new TokenQueue(program);
            InputOutput io = InputOutput.getInstance();
            Context context = new Context(io);
            Builder.parseAndExecute(tokenQueue, context);
            assertFalse(true);
        } catch (TypeException e) {
            assertThat(e.getMessage(), containsString("near line 5"));
        }
    }
}
