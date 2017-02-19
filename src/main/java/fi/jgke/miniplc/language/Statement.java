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

import fi.jgke.miniplc.tokenizer.Token;
import fi.jgke.miniplc.tokenizer.TokenQueue;
import fi.jgke.miniplc.tokenizer.TokenValue;
import fi.jgke.miniplc.exception.AssertionFailureException;
import fi.jgke.miniplc.exception.TypeException;
import fi.jgke.miniplc.exception.UnexpectedTokenException;
import fi.jgke.miniplc.interpreter.*;
import fi.jgke.miniplc.exception.RuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Statement implements Executable {

    private class CreateVariable implements Executable {

        private class UninitializedVariable implements ExecutableWithResult {
            VariableType type;
            public UninitializedVariable(VariableType type) {
                this.type = type;
            }
            @Override
            public void parse(TokenQueue tokens) throws RuntimeException {
                throw new IllegalStateException();
            }

            @Override
            public Variable execute(Context context) throws RuntimeException {
                return new Variable(type);
            }
        }

        String variableName;
        VariableType type;
        Optional<ExecutableWithResult> value;

        @Override
        public void parse(TokenQueue tokens) throws RuntimeException {
            Token identifier = tokens.getExpectedToken(TokenValue.IDENTIFIER);
            tokens.getExpectedToken(TokenValue.COLON);
            Token typeToken = tokens.getExpectedToken(TokenValue.INT, TokenValue.STRING, TokenValue.BOOL);

            variableName = identifier.getString();
            type = typeToken.getVariableType();

            if (tokens.peek().getValue() == TokenValue.ASSIGN) {
                tokens.getExpectedToken(TokenValue.ASSIGN);
                Expression expression = new Expression();
                expression.parse(tokens);
                value = Optional.of(expression);
            } else {
                value = Optional.empty();
            }
        }

        @Override
        public void execute(Context context) throws RuntimeException {
            ExecutableWithResult executable = this.value.orElse(new UninitializedVariable(type));
            Object value = executable.execute(context).getNullableValue();
            Variable variable;
            if(value != null) {
                variable = new Variable(variableName, type, value);
            } else {
                variable = new Variable(variableName, type);
            }
            context.addVariable(variable);
        }
    }

    private class Assign implements Executable {
        String identifier;
        Expression value;

        public Assign(Token identifier) {
            this.identifier = identifier.getString();
        }

        @Override
        public void parse(TokenQueue tokens) throws RuntimeException {
            tokens.getExpectedToken(TokenValue.ASSIGN);

            value = new Expression();
            value.parse(tokens);
        }

        @Override
        public void execute(Context context) throws RuntimeException {
            VariableType type = context.getVariable(identifier).getType();
            Variable variable = value.execute(context);
            variable.setName(identifier);

            if (!variable.getType().equals(type)) {
                throw new TypeException(type, variable.getType());
            }

            context.updateVariable(variable);
        }
    }

    private class For implements Executable {

        Expression loopStart;
        Expression loopEnd;
        String loopVariableName;
        Statements loopBody;

        @Override
        public void parse(TokenQueue tokens) throws RuntimeException {
            loopVariableName = tokens.getExpectedToken(TokenValue.IDENTIFIER).getString();
            tokens.getExpectedToken(TokenValue.IN);

            loopStart = new Expression();
            loopStart.parse(tokens);

            tokens.getExpectedToken(TokenValue.RANGE);

            loopEnd = new Expression();
            loopEnd.parse(tokens);

            tokens.getExpectedToken(TokenValue.DO);
            loopBody = new Statements();
            loopBody.parse(tokens);

            tokens.getExpectedToken(TokenValue.END);
            tokens.getExpectedToken(TokenValue.FOR);
        }

        @Override
        public void execute(Context context) throws RuntimeException {
            Integer low = (Integer) loopStart.execute(context).getValue();
            Integer high = (Integer) loopEnd.execute(context).getValue();

            Variable loopVariable = new Variable(loopVariableName, VariableType.INT, low);
            context.updateVariable(loopVariable);

            for (Integer i = low; i <= high; i++) {
                context.pushFrame();

                loopBody.execute(context);

                context.popFrame();
                loopVariable = new Variable(loopVariableName, VariableType.INT, i+1);
                context.updateVariable(loopVariable);
            }
        }
    }

    private class Read implements Executable {
        String identifier;

        @Override
        public void parse(TokenQueue tokens) throws RuntimeException {
            identifier = tokens.getExpectedToken(TokenValue.IDENTIFIER).getString();
        }

        @Override
        public void execute(Context context) throws RuntimeException {
            String input = context.readLine();
            Variable variable;

            /* Why, MiniPL language ;__;
             * print :: () -> Int
             * print :: () -> String
             */
            try {
                Integer value = Integer.parseInt(input);
                variable = new Variable(identifier, VariableType.INT, value);
            } catch (NumberFormatException ignored) {
                variable = new Variable(identifier, VariableType.STRING, input);
            }

            context.updateVariable(variable);
        }
    }

    private class Print implements Executable {
        Expression value;

        @Override
        public void parse(TokenQueue tokens) throws RuntimeException {
            value = new Expression();
            value.parse(tokens);
        }

        @Override
        public void execute(Context context) throws RuntimeException {
            Object value = this.value.execute(context).getValue();
            context.print(value);
        }
    }

    private class Assert implements Executable {
        Expression value;

        @Override
        public void parse(TokenQueue tokens) throws RuntimeException {
            value = new Expression();
            value.parse(tokens);
        }

        @Override
        public void execute(Context context) throws RuntimeException {
            Boolean value = (Boolean) this.value.execute(context).getValue();
            if (!value) {
                throw new AssertionFailureException();
            }
        }
    }

    List<Executable> contents;

    public Statement() {
        contents = new ArrayList<>();
    }

    /*
    <stmt>   ::=  "var" <var_ident> ":" <type> [ ":=" <expr> ]
              |   <var_ident> ":=" <expr>
              |   "for" <var_ident> "in" <expr> ".." <expr> "do"
                     <stmts> "end" "for"
              |   "read" <var_ident>
              |   "print" <expr>
              |   "assert" "(" <expr> ")"
    */
    @Override
    public void parse(TokenQueue tokens) throws RuntimeException {
        Token token = tokens.remove();
        Executable content;
        switch (token.getValue()) {
            case VAR:
                content = new CreateVariable();
                break;
            case IDENTIFIER:
                content = new Assign(token);
                break;
            case FOR:
                content = new For();
                break;
            case READ:
                content = new Read();
                break;
            case PRINT:
                content = new Print();
                break;
            case ASSERT:
                content = new Assert();
                break;
            default:
                throw new UnexpectedTokenException(token);
        }
        content.parse(tokens);
        this.contents.add(content);
    }

    @Override
    public void execute(Context context) throws RuntimeException {
        for (Executable executable : this.contents) {
            executable.execute(context);
        }
    }
}
