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

import fi.jgke.miniplc.exception.EndOfInputException;
import fi.jgke.miniplc.exception.UnexpectedCharacterException;
import fi.jgke.miniplc.interpreter.VariableType;

import java.util.*;

public class Tokenizer {

    private static final Map<Character, Character> escapeMap;
    private static final Map<String, TokenValue> keywords;
    private static final Map<Character, TokenValue> simpleTokens;
    private static final Map<TokenValue, Object> values;

    private int lineNumber;

    public Tokenizer() {
        lineNumber = 1;
    }

    static {
        escapeMap = getEscapeCharacters();
        keywords = getKeywords();
        simpleTokens = getSimpleTokens();
        values = getTypeValues();
    }

    private static Map<Character, Character> getEscapeCharacters() {
        Map<Character, Character> escapeCharacters = new HashMap<>();
        escapeCharacters.put('"', '"');
        escapeCharacters.put('n', '\n');
        escapeCharacters.put('t', '\t');
        return escapeCharacters;
    }

    private static Map<String, TokenValue> getKeywords() {
        Map<String, TokenValue> keywords = new HashMap<>();
        keywords.put("assert", TokenValue.ASSERT);
        keywords.put("bool", TokenValue.BOOL);
        keywords.put("do", TokenValue.DO);
        keywords.put("end", TokenValue.END);
        keywords.put("for", TokenValue.FOR);
        keywords.put("in", TokenValue.IN);
        keywords.put("int", TokenValue.INT);
        keywords.put("print", TokenValue.PRINT);
        keywords.put("read", TokenValue.READ);
        keywords.put("string", TokenValue.STRING);
        keywords.put("var", TokenValue.VAR);
        return keywords;
    }

    private static Map<Character, TokenValue> getSimpleTokens() {
        Map<Character, TokenValue> simpleTokens = new HashMap<>();
        simpleTokens.put('!', TokenValue.NOT);
        simpleTokens.put('&', TokenValue.AND);
        simpleTokens.put('(', TokenValue.OPEN_BRACE);
        simpleTokens.put(')', TokenValue.CLOSE_BRACE);
        simpleTokens.put('*', TokenValue.TIMES);
        simpleTokens.put('+', TokenValue.PLUS);
        simpleTokens.put('-', TokenValue.MINUS);
        simpleTokens.put(';', TokenValue.SEMICOLON);
        simpleTokens.put('<', TokenValue.LESS_THAN);
        simpleTokens.put('=', TokenValue.EQUALS);
        return simpleTokens;
    }

    private static Map<TokenValue, Object> getTypeValues() {
        Map<TokenValue, Object> values = new HashMap<>();
        values.put(TokenValue.BOOL, VariableType.BOOL);
        values.put(TokenValue.INT, VariableType.INT);
        values.put(TokenValue.STRING, VariableType.STRING);
        return values;
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
        flushWhitespace(input);
        if (input.isEmpty())
            return new Token(TokenValue.EOS);

        Character character = input.remove();

        if (simpleTokens.containsKey(character))
            return new Token(simpleTokens.get(character));

        switch (character) {
            case '/':
                return handleCommentOrDivide(input);

            case ':':
                return handleAssignOrColon(input);

            case '.':
                return handleRange(input);

            case '"':
                return handleString(input);
        }

        if (isNumber(character))
            return handleNumber(input, character);

        if (isLetter(character))
            return handleToken(input, character);

        throw new UnexpectedCharacterException(lineNumber, character);
    }

    private Token handleAssignOrColon(Queue<Character> input) {
        if (!input.isEmpty() && input.peek() == '=') {
            input.remove();
            return new Token(TokenValue.ASSIGN);
        }
        return new Token(TokenValue.COLON);
    }

    private Token handleCommentOrDivide(Queue<Character> input) {
        if (!input.isEmpty()) {
            if (input.peek() == '/') {
                return handleSingleLineComment(input);

            } else if (input.peek() == '*') {
                input.remove();
                if (!input.isEmpty() && input.element() == '\n') {
                    lineNumber++;
                }

                return handleMultilineComment(input);
            }
        }
        return new Token(TokenValue.DIVIDE);
    }

    private void flushWhitespace(Queue<Character> input) {
        while (!input.isEmpty() && Character.isWhitespace(input.element())) {
            Character character = input.remove();
            if (character == '\n')
                lineNumber++;
        }
    }

    private Token handleRange(Queue<Character> input) {
        if (input.isEmpty()) {
            throw new EndOfInputException();
        }
        char c = input.remove();
        if (c != '.') {
            throw new UnexpectedCharacterException(lineNumber, c);
        }
        return new Token(TokenValue.RANGE);
    }

    private Token handleSingleLineComment(Queue<Character> input) {
        while (!input.isEmpty())
            if (input.remove() == '\n')
                break;
        lineNumber++;
        return readToken(input);
    }

    private Token handleToken(Queue<Character> input, Character initial) {
        String token = "" + initial;
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
            case "bool":
                return new Token(TokenValue.TYPE, VariableType.BOOL);
            case "int":
                return new Token(TokenValue.TYPE, VariableType.INT);
            case "string":
                return new Token(TokenValue.TYPE, VariableType.STRING);
            case "true":
                return new Token(TokenValue.BOOL_CONST, true);
            case "false":
                return new Token(TokenValue.BOOL_CONST, false);
        }
        TokenValue type = keywords.getOrDefault(token, TokenValue.IDENTIFIER);
        Object tokenValue = values.getOrDefault(type, token);
        return new Token(type, tokenValue);
    }


    private Token handleNumber(Queue<Character> input, Character initial) {
        String token = "" + initial;
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

    private Token handleString(Queue<Character> input) {
        String token = "";
        while (true) {
            if (input.isEmpty()) {
                throw new EndOfInputException();
            }
            char c = input.remove();
            if (c == '\n')
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

    private Token handleMultilineComment(Queue<Character> input) {
        try {
            while (input.remove() != '*' || input.element() != '/') {
                if (input.peek() == '\n') {
                    lineNumber++;
                }
            }
            // remove the '/'
            input.remove();
        } catch (NoSuchElementException e) {
            throw new EndOfInputException();
        }

        return readToken(input);
    }

    public Queue<Token> tokenize(String input) throws UnexpectedCharacterException {
        Queue<Token> tokenQueue = new ArrayDeque<>();
        Queue<Character> queue = new ArrayDeque<>();
        for (Character c : input.toCharArray())
            queue.add(c);
        while (!queue.isEmpty()) {
            Token token = readToken(queue);
            if (token.getValue().equals(TokenValue.EOS))
                break;
            tokenQueue.add(token);
        }
        tokenQueue.add(new Token(TokenValue.EOS));
        return tokenQueue;
    }
}
