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
package fi.jgke.miniplc.tokenizer;

import fi.jgke.miniplc.exception.EndOfInputException;
import fi.jgke.miniplc.interpreter.VariableType;
import fi.jgke.miniplc.exception.UnexpectedCharacterException;
import fi.jgke.miniplc.exception.UnexpectedTokenException;

import java.util.*;

public class TokenQueue {
    private final ArrayDeque<Token> tokens;

    private final Map<Character, Character> escapeMap;
    private final Map<String, TokenValue> keywords;
    private final Map<TokenValue, Object> values;
    public static int lineNumber = 1;

    public TokenQueue(String file) throws UnexpectedCharacterException {
        tokens = new ArrayDeque<>();

        escapeMap = new HashMap<>();
        escapeMap.put('n', '\n');
        escapeMap.put('"', '"');
        escapeMap.put('t', '\t');

        keywords = new HashMap<>();
        keywords.put("var", TokenValue.VAR);
        keywords.put("for", TokenValue.FOR);
        keywords.put("end", TokenValue.END);
        keywords.put("in", TokenValue.IN);
        keywords.put("do", TokenValue.DO);
        keywords.put("read", TokenValue.READ);
        keywords.put("print", TokenValue.PRINT);
        keywords.put("int", TokenValue.INT);
        keywords.put("string", TokenValue.STRING);
        keywords.put("bool", TokenValue.BOOL);
        keywords.put("assert", TokenValue.ASSERT);

        values = new HashMap<>();
        values.put(TokenValue.INT, VariableType.INT);
        values.put(TokenValue.STRING, VariableType.STRING);
        values.put(TokenValue.BOOL, VariableType.BOOL);

        this.tokenize(file);
    }

    public static TokenQueue of(Token... tokens) {
        TokenQueue tokenQueue = new TokenQueue("");
        tokenQueue.getExpectedToken(TokenValue.EOS);
        for (Token t : tokens)
            tokenQueue.add(t);
        return tokenQueue;
    }

    private boolean isNumber(Character c) {
        return c != null && c >= '0' && c <= '9';
    }

    private boolean isLetter(Character c) {
        return c != null && ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }

    private boolean isIdentifierCharacter(Character c) {
        return c != null && (isNumber(c) || isLetter(c));
    }

    private Token readToken(Queue<Character> input) throws UnexpectedCharacterException {
        Character beginning = input.remove();
        while (Character.isWhitespace(beginning)) {
            if (beginning == '\n')
                lineNumber++;
            if (input.isEmpty())
                return new Token(TokenValue.EOS);
            beginning = input.remove();
        }
        switch (beginning) {
            /* Simple syntax */
            case ';':
                return new Token(TokenValue.SEMICOLON);
            case '(':
                return new Token(TokenValue.OPEN_BRACE);
            case ')':
                return new Token(TokenValue.CLOSE_BRACE);

            /* Operators, simple cases */
            case '+':
                return new Token(TokenValue.PLUS);
            case '-':
                return new Token(TokenValue.MINUS);
            case '*':
                return new Token(TokenValue.TIMES);
            case '&':
                return new Token(TokenValue.AND);
            case '!':
                return new Token(TokenValue.NOT);
            case '<':
                return new Token(TokenValue.LESS_THAN);
            case '=':
                return new Token(TokenValue.EQUALS);

            /* Comment or divide */
            case '/':
                if (!input.isEmpty() && input.peek() == '/') {
                    /* It's a comment, remove the rest of the line */
                    while (!input.isEmpty() && input.remove() != '\n') {}
                    lineNumber++;
                    return readToken(input);
                } else if (!input.isEmpty() && input.peek() == '*') {
                    input.remove();
                    if(!input.isEmpty() && input.element() == '\n') {
                        lineNumber++;
                    }
                    /* Multiline comment, remove until end of the multiline comment */
                    try {
                        while (input.remove() != '*' || input.element() != '/') {
                            if (input.peek() == '\n') {
                                lineNumber++;
                            }
                        }
                    } catch (NoSuchElementException e) {
                        throw new EndOfInputException();
                    }

                    /* Poll because input might be empty here, so readToken returns EOS */
                    input.poll();
                    return readToken(input);
                }
                return new Token(TokenValue.DIVIDE);

            /* Assignment or type definition */
            case ':':
                if (!input.isEmpty() && input.peek() == '=') {
                    input.remove();
                    return new Token(TokenValue.ASSIGN);
                }
                return new Token(TokenValue.COLON);

            case '.':
                if (input.isEmpty()) {
                    throw new EndOfInputException();
                }
                char c = input.remove();
                if (c != '.') {
                    throw new UnexpectedCharacterException(lineNumber, c);
                }
                return new Token(TokenValue.RANGE);
        }

        String token = "";

        if (beginning == '"') {
            while (true) {
                if (input.isEmpty()) {
                    throw new EndOfInputException();
                }
                char c = input.remove();
                if(c == '\n')
                    lineNumber++;
                if (c == '"')
                    return new Token(TokenValue.STRING_CONST, token);
                if (c == '\\') {
                    char cc = input.remove();
                    c = escapeMap.getOrDefault(cc, cc);
                }
                token += c;
            }
        }

        token += beginning;

        if (!isLetter(beginning) && !isNumber(beginning)) {
            throw new UnexpectedCharacterException(lineNumber, beginning);
        }

        if (beginning >= '0' && beginning <= '9') {
            while (true) {
                Character c = input.peek();
                if (isNumber(c))
                    token += c;
                else if (isLetter(c))
                    throw new UnexpectedCharacterException(lineNumber, c);
                else
                    return new Token(TokenValue.INT_CONST, Integer.parseInt(token));
                input.remove();
            }
        }

        while (true) {
            if (input.isEmpty()) {
                return getTokenFromWord(token);
            }

            Character c = input.peek();
            if (isIdentifierCharacter(c))
                token += c;
            else {
                return getTokenFromWord(token);
            }
            input.remove();
        }
    }

    private Token getTokenFromWord(String token) {
        switch (token) {
            case "true":
                return new Token(TokenValue.BOOL_CONST, true);
            case "false":
                return new Token(TokenValue.BOOL_CONST, false);
            case "int":
                return new Token(TokenValue.TYPE, VariableType.INT);
            case "string":
                return new Token(TokenValue.TYPE, VariableType.STRING);
            case "bool":
                return new Token(TokenValue.TYPE, VariableType.BOOL);
        }
        TokenValue type = keywords.getOrDefault(token, TokenValue.IDENTIFIER);
        Object tokenValue = values.getOrDefault(type, token);
        return new Token(type, tokenValue);
    }

    private void tokenize(String input) throws UnexpectedCharacterException {
        Queue<Character> queue = new ArrayDeque<>();
        for (Character c : input.toCharArray())
            queue.add(c);
        while (!queue.isEmpty()) {
            Token token = readToken(queue);
            if (token.getValue().equals(TokenValue.EOS))
                break;
            this.add(token);
        }
        this.add(new Token(TokenValue.EOS));
    }


    private void add(Token token) {
        tokens.add(token);
    }

    public Token getExpectedToken(TokenValue... types) throws UnexpectedTokenException {
        Token token = this.remove();
        for (TokenValue type : types) {
            if (token.getValue().equals(type))
                return token;
        }
        throw new UnexpectedTokenException(token, types);
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
