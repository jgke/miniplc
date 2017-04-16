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

import fi.jgke.miniplc.exception.UnexpectedCharacterException;
import fi.jgke.miniplc.exception.UnexpectedTokenException;

import java.util.Collections;
import java.util.Queue;

public class TokenQueue {
    public static int lineNumber = 1;
    private Queue<Token> tokens;

    public TokenQueue(String input) throws UnexpectedCharacterException {
        tokens = new Tokenizer().tokenize(input);
    }

    public static TokenQueue of(Token... tokens) {
        TokenQueue tokenQueue = new TokenQueue("");
        tokenQueue.getExpectedToken(TokenValue.EOS);
        Collections.addAll(tokenQueue.tokens, tokens);
        return tokenQueue;
    }

    public Token getExpectedToken(TokenValue type) throws UnexpectedTokenException {
        Token token = this.remove();
        if (token.getValue().equals(type))
            return token;
        throw new UnexpectedTokenException(token, type);
    }

    public Token remove() {
        return tokens.remove();
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    public Token element() {
        return tokens.element();
    }
}
