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
package fi.jgke.miniplc.interpreter;

import fi.jgke.miniplc.Token;
import fi.jgke.miniplc.TokenValue;

import java.util.*;
import java.util.function.Consumer;

public class TokenQueue implements Iterable<Token> {
    ArrayDeque<Token> tokens;

    public TokenQueue() {
        tokens = new ArrayDeque<>();
    }

    public void add(Token token) {
        tokens.add(token);
    }

    public Token getExpectedToken(TokenValue... types) throws UnexpectedTypeException {
        Token token = this.remove();
        for (TokenValue type : types) {
            if (token.getValue().equals(type))
                return token;
        }
        throw new UnexpectedTypeException(token.getValue(), types);
    }


    public Token remove() {
        return tokens.remove();
    }

    public Token peek() {
        return tokens.peek();
    }

    @Override
    public Iterator<Token> iterator() {
        return tokens.iterator();
    }

    @Override
    public void forEach(Consumer<? super Token> consumer) {
        tokens.forEach(consumer);
    }

    @Override
    public Spliterator<Token> spliterator() {
        return tokens.spliterator();
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }
}
