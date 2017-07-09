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

package fi.jgke.miniplc;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testNormalFlow() throws Exception {
        Path p = Files.createTempFile(null, null);
        try (OutputStream s = Files.newOutputStream(p.toAbsolutePath())) {
            s.write("print \"hello world\";".getBytes());
            s.close();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(outputStream);
            String[] args = {p.toAbsolutePath().toString()};
            int status = Miniplc.app(args, ps, null);
            assertEquals(status, 0);
            assertTrue(outputStream.toString().contains("hello world"));
        } finally {
            Files.delete(p);
        }
    }

    @Test
    public void testNonexistentFile() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outputStream);
        int status = Miniplc.app(new String[]{"/nonexistent/file"}, null, ps);
        assertNotEquals(status, 0);
        assertTrue(outputStream.toString().contains("file not found"));
    }

    @Test
    public void testNoArguments() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outputStream);
        int status = Miniplc.app(new String[]{}, null, ps);
        assertNotEquals(status, 0);
        assertTrue(outputStream.toString().contains("Invalid number of arguments"));
    }

    @Test
    public void testRuntimeError() throws Exception {
        Path p = Files.createTempFile(null, null);
        try (OutputStream s = Files.newOutputStream(p.toAbsolutePath())) {
            s.write("syntax error".getBytes());
            s.close();
            String[] args = {p.toAbsolutePath().toString()};

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(outputStream);
            int status = Miniplc.app(args,null, ps);
            assertNotEquals(status, 0);
            assertTrue(outputStream.toString().contains("Unexpected token"));
        } finally {
            Files.delete(p);
        }
    }
}
