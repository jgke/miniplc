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

import fi.jgke.miniplc.exception.RuntimeException;
import fi.jgke.miniplc.interpreter.Executor;
import fi.jgke.miniplc.interpreter.InputOutput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class Examples {
    @Mock
    private
    InputOutput inputOutput;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void example1() throws RuntimeException {
        String sample = "" +
                "var X : int := 4 + (6 * 2);\n" +
                "print X;";

        Executor executor = new Executor(sample);
        executor.execute(inputOutput);

        verify(inputOutput).print(16);
        verify(inputOutput, never()).readLine();
        verifyNoMoreInteractions(inputOutput);
    }

    @Test
    public void example2() throws RuntimeException {
        String sample = "" +
                "var nTimes : int := 0;\n" +
                "print \"How many times?\"; \n" +
                "read nTimes; \n" +
                "var x : int;\n" +
                "for x in 0..nTimes-1 do \n" +
                "    print x;\n" +
                "    print \" : Hello, World!\\n\";\n" +
                "end for;\n" +
                "assert (x = nTimes);\n";

        when(inputOutput.readLine()).thenReturn("3");
        Executor executor = new Executor(sample);
        executor.execute(inputOutput);

        InOrder inOrder = inOrder(inputOutput);
        inOrder.verify(inputOutput).print("How many times?");
        inOrder.verify(inputOutput).readLine();
        inOrder.verify(inputOutput).print(0);
        inOrder.verify(inputOutput).print(" : Hello, World!\n");
        inOrder.verify(inputOutput).print(1);
        inOrder.verify(inputOutput).print(" : Hello, World!\n");
        inOrder.verify(inputOutput).print(2);
        inOrder.verify(inputOutput).print(" : Hello, World!\n");
        verifyNoMoreInteractions(inputOutput);
    }

    @Test
    public void example3() throws RuntimeException {
        String sample = "print \"Give a number\"; \n" +
                "     var n : int;\n" +
                "     read n;\n" +
                "     var v : int := 1;\n" +
                "     var i : int;\n" +
                "     for i in 1..n do \n" +
                "         v := v * i;\n" +
                "     end for;\n" +
                "     print \"The result is: \";\n" +
                "     print v;";
        when(inputOutput.readLine()).thenReturn("5");
        Executor executor = new Executor(sample);
        executor.execute(inputOutput);

        InOrder inOrder = inOrder(inputOutput);
        inOrder.verify(inputOutput).print("Give a number");
        inOrder.verify(inputOutput).readLine();
        inOrder.verify(inputOutput).print(120);
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
