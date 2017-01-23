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

import fi.jgke.miniplc.interpreter.*;
import fi.jgke.miniplc.interpreter.RuntimeException;
import fi.jgke.miniplc.language.Program;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Miniplc {

    private static final Map<Character, Character> escapeMap;
    private static final Map<String, TokenValue> keywords;
    private static final Map<TokenValue, Object> values;

    static {
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
    }

    private static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean isIdentifierCharacter(char c) {
        return isNumber(c) || isLetter(c);
    }

    private static Token readToken(Queue<Character> input) throws UnexpectedCharacterException {
        char beginning = input.remove();
        while (Character.isWhitespace(beginning)) {
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
                return new Token(TokenValue.LESSTHAN);
            case '=':
                return new Token(TokenValue.EQUALS);

            /* Comment or times */
            case '/':
                if (input.peek() == '/') {
                    /* It's a comment, remove the rest of the line */
                    while (input.remove() != '\n') ;
                    return readToken(input);
                } else if (input.peek() == '*') {
                    /* Multiline comment, remove until end of the multiline comment */
                    while (input.remove() != '*' && input.peek() != '/') ;
                    return readToken(input);
                }
                return new Token(TokenValue.TIMES);

            /* Assignment or type definition */
            case ':':
                if (input.peek() == '=') {
                    input.remove();
                    return new Token(TokenValue.ASSIGN);
                }
                return new Token(TokenValue.COLON);

            case '.':
                char c = input.remove();
                if (c != '.') {
                    throw new UnexpectedCharacterException(c);
                }
                return new Token(TokenValue.RANGE);
        }

        String token = "";

        if (beginning == '"') {
            while (true) {
                char c = input.remove();
                if (c == '"')
                    return new Token(TokenValue.STRINGVAR, token);
                if (c == '\\') {
                    char cc = input.remove();
                    c = escapeMap.getOrDefault(cc, cc);
                }
                token += c;
            }
        }

        token += beginning;

        if (!isLetter(beginning) && !isNumber(beginning)) {
            throw new UnexpectedCharacterException(beginning);
        }

        if (beginning >= '0' && beginning <= '9') {
            while (true) {
                char c = input.peek();
                if (isNumber(c))
                    token += c;
                else if (isLetter(c))
                    throw new UnexpectedCharacterException(c);
                else
                    return new Token(TokenValue.INTVAR, Integer.parseInt(token));
                input.remove();
            }
        }

        while (true) {
            char c = input.peek();
            if (isIdentifierCharacter(c))
                token += c;
            else {
                TokenValue type = keywords.getOrDefault(token, TokenValue.IDENTIFIER);
                Object tokenValue = values.getOrDefault(type, token);
                return new Token(type, tokenValue);
            }
            input.remove();
        }
    }

    private static TokenQueue tokenize(String input) throws UnexpectedCharacterException {
        TokenQueue tokenQueue = new TokenQueue();
        Queue<Character> queue = new ArrayDeque<>();
        for (Character c : input.toCharArray())
            queue.add(c);
        while (!queue.isEmpty()) {
            Token token = readToken(queue);
            if (token.getValue().equals(TokenValue.EOS))
                break;
            tokenQueue.add(token);
        }
        return tokenQueue;
    }

    public static void main(String[] args) throws UnexpectedCharacterException, RuntimeException {
        String[] samples = {
                "var X : int := 4 + (6 * 2);\n" +
                        "print X;" ,

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

        /* Comment these out for real input */
        InputOutput.addNextLine("5"); // sample 2
        InputOutput.addNextLine("4"); // sample 3

        for (String s : samples) {
            TokenQueue tokenQueue = tokenize(s);

            Program program = new Program();
            program.parse(tokenQueue);
            program.execute(new Stack());
        }
    }
}
