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

import fi.jgke.miniplc.exception.RuntimeException;
import fi.jgke.miniplc.interpreter.Executor;
import fi.jgke.miniplc.interpreter.InputOutput;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Miniplc {

    public static int app(String[] args, PrintStream out, PrintStream err) throws IOException {
        if (args.length != 1) {
            err.println("Invalid number of arguments: expected one");
            return -1;
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            err.println("Invalid argument: file not found");
            return -1;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(args[0])));
            Executor executor = new Executor(content);
            executor.execute(new InputOutput(out));
        } catch (RuntimeException e) {
            err.println(e.getMessage());
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) throws IOException {
        System.exit(app(args, System.out, System.err));
    }

}
