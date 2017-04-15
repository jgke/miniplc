/*
 * Copyright 2016 Jaakko Hannikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fi.jgke.miniplc;

import fi.jgke.miniplc.exception.*;
import fi.jgke.miniplc.exception.RuntimeException;
import fi.jgke.miniplc.interpreter.Executor;
import fi.jgke.miniplc.interpreter.InputOutput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Miniplc {

    public static void main(String[] args) throws UnexpectedCharacterException, RuntimeException, IOException {
        if (args.length != 1) {
            System.err.println("Invalid number of arguments: expected one");
            System.exit(-1);
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            System.err.println("Invalid argument: file not found");
            System.exit(-1);
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(args[0])));
            Executor executor = new Executor(content);
            executor.execute(InputOutput.getInstance());
        } catch(RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
