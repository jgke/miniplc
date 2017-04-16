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
package fi.jgke.miniplc.tokenizer;

import fi.jgke.miniplc.interpreter.VariableType;

public class Token {
    private final TokenValue value;
    private final Object content;

    public int getLineNumber() {
        return lineNumber;
    }

    private final int lineNumber;

    public Token(TokenValue value, Object content) {
        this.value = value;
        this.content = content;
        this.lineNumber = TokenQueue.getLineNumber();
    }

    public Token(TokenValue value) {
        this.value = value;
        this.content = null;
        this.lineNumber = TokenQueue.getLineNumber();
    }

    public TokenValue getValue() {
        return value;
    }

    public Object getContent() {
        return content;
    }

    public VariableType getVariableType() {
        return (VariableType) content;
    }

    public String getString() {
        return (String) content;
    }

    @Override
    public String toString() {
        String newline = this.value.equals(TokenValue.SEMICOLON) || this.value.equals(TokenValue.DO) ? "\n" : " ";
        return "[" + value + "=" + content + "]" + newline;
    }
}
