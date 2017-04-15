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
package fi.jgke.miniplc.exception;

import fi.jgke.miniplc.tokenizer.Token;
import fi.jgke.miniplc.interpreter.VariableType;

public class OperationNotSupportedException extends RuntimeException {
    public OperationNotSupportedException(VariableType type, Token operator) {
        super("Unsupported operation near line " + operator.getLineNumber() + ": Cannot use " + operator + " with " + type);
    }

    public OperationNotSupportedException(Token not) {
        super("Unsupported operation near line " + not.getLineNumber() + ": Cannot use " + not.getValue() + " with non-boolean");
    }
}
