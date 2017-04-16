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

package fi.jgke.miniplc.unit;

import fi.jgke.miniplc.builder.SimpleConsumedRule;
import fi.jgke.miniplc.builder.Terminal;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import fi.jgke.miniplc.tokenizer.Token;
import org.junit.Test;

import static fi.jgke.miniplc.tokenizer.TokenValue.DO;
import static fi.jgke.miniplc.tokenizer.TokenValue.MINUS;
import static fi.jgke.miniplc.tokenizer.TokenValue.SEMICOLON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class ToStrings {
    @Test
    public void simpleConsumedRule() throws Exception {
        new SimpleConsumedRule("foo").toString();
    }

    @Test
    public void variable() {
        new Variable(VariableType.BOOL, true).toString();
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
}
