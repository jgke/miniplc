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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import fi.jgke.miniplc.exception.RuntimeException;
import org.junit.*;
import org.mockito.ArgumentCaptor;

import java.util.List;

public class ExecutorTest {

    private InputOutput io;

    @Before
    public void setUp() throws Exception {
        this.io = mock(InputOutput.class);
    }

    @Test
    public void testOne() throws RuntimeException {
        String s = "var X : int := 4 + (6 * 2);\n" +
                "print X;";
        Executor executor = new Executor(s);
        executor.execute(io);

        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        verify(io).print(argument.capture());

        Object value = argument.getValue();
        assertTrue(value instanceof Integer);
        assertEquals(value, 16);
    }

    @Test
    public void testTwo() throws RuntimeException {
        String s = "var nTimes : int := 0;\n" +
                "print \"How many times?\"; \n" +
                "read nTimes; \n" +
                "var x : int;\n" +
                "for x in 0..nTimes-1 do \n" +
                "    print x;\n" +
                "    print \" : Hello, World!\\n\";\n" +
                "end for;\n" +
                "assert (x = nTimes);\n";
        Executor executor = new Executor(s);

        when(io.readLine()).thenReturn("5");
        executor.execute(io);

        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        verify(io, times(11)).print(argument.capture());

        List<Object> results = argument.getAllValues();

        assertEquals(results.get(0), "How many times?");

        for (int i = 0; i < 5; i++) {
            Object value = results.get(i * 2 + 1);
            assertTrue(value instanceof Integer);
            assertEquals(value, i);

            value = results.get(i * 2 + 2);
            assertTrue(value instanceof String);
            assertEquals(value, " : Hello, World!\n");
        }
    }

    @Test
    public void testThree() throws RuntimeException {
        String s = "print \"Give a number\"; \n" +
                "     var n : int;\n" +
                "     read n;\n" +
                "     var v : int := 1;\n" +
                "     var i : int;\n" +
                "     for i in 1..n do \n" +
                "         v := v * i;\n" +
                "     end for;\n" +
                "     print \"The result is: \";\n" +
                "     print v;";
        Executor executor = new Executor(s);
        when(io.readLine()).thenReturn("5");

        executor.execute(io);

        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        verify(io, times(3)).print(argument.capture());

        List<Object> results = argument.getAllValues();

        assertEquals(results.get(0), "Give a number");
        assertEquals(results.get(1), "The result is: ");
        assertEquals(results.get(2), 120);
    }
}