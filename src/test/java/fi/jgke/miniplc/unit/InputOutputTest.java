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

import fi.jgke.miniplc.interpreter.InputOutput;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class InputOutputTest {
    @Test
    public void testReadString() throws Exception {
        InputStream in = System.in;
        try {
            String input = "foo";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            String read = InputOutput.getInstance().readLine();
            assertEquals(input, read);
        } finally {
            System.setIn(in);
        }
    }
}
