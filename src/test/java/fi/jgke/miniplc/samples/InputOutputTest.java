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
package fi.jgke.miniplc.samples;

import fi.jgke.miniplc.interpreter.Executor;
import fi.jgke.miniplc.interpreter.InputOutput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InputOutputTest {
    @Mock
    private
    InputOutput inputOutput;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void strings() {
        String sample = "var n : string := \"foo\" + \"bar\";\n" +
                "read n;\n" +
                "print n;";
        when(inputOutput.readLine()).thenReturn("string");
        new Executor(sample).execute(inputOutput);
        verify(inputOutput).print("string");
    }
}
