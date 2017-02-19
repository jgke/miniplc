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

import fi.jgke.miniplc.exception.RuntimeException;
import fi.jgke.miniplc.exception.UnexpectedCharacterException;

public class Miniplc {

    public static void main(String[] args) throws UnexpectedCharacterException, RuntimeException {
        String[] samples = {
                "var X : int := 4 + (6 * 2);\n" +
                        "print X;",

                "var nTimes : int := 0;\n" +
                        "print \"How many times?\"; \n" +
                        "read nTimes; \n" +
                        "var x : int;\n" +
                        "for x in 0..nTimes-1 do \n" +
                        "    print x;\n" +
                        "    print \" : Hello, World!\\n\";\n" +
                        "end for;\n" +
                        "assert (x = nTimes);\n",

                "print \"Give a number\"; \n" +
                        "     var n : int;\n" +
                        "     read n;\n" +
                        "     var v : int := 1;\n" +
                        "     var i : int;\n" +
                        "     for i in 1..n do \n" +
                        "         v := v * i;\n" +
                        "     end for;\n" +
                        "     print \"The result is: \";\n" +
                        "     print v;"
        };
/*
        for (String s : samples) {
            Executor executor = new Executor(s);
            executor.execute(InputOutput.getInstance());
        }
        */
    }
}
