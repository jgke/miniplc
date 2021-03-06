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

import java.io.PrintStream;
import java.util.Scanner;

public class InputOutput {
    private final Scanner input;
    private final PrintStream output;

    public InputOutput(PrintStream out) {
        input = new Scanner(System.in);
        output = out;
    }

    public static InputOutput getInstance() {
        return new InputOutput(System.out);
    }

    public String readLine() {
        return input.nextLine();
    }

    public void print(Object output) {
        this.output.print(output);
    }
}
