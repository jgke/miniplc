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
package fi.jgke.miniplc.language;

import fi.jgke.miniplc.interpreter.RuntimeException;
import fi.jgke.miniplc.interpreter.Stack;
import fi.jgke.miniplc.interpreter.TokenQueue;
import fi.jgke.miniplc.interpreter.Variable;

import java.util.Optional;

public interface Executable {
    void parse(TokenQueue tokens) throws RuntimeException;
    void execute(Stack stack) throws RuntimeException;
}
