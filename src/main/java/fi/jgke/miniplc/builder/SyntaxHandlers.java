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

package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.exception.*;
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import fi.jgke.miniplc.tokenizer.Token;
import fi.jgke.miniplc.tokenizer.TokenValue;

import java.util.List;

public class SyntaxHandlers {
    public static Object handleConstant(List<ConsumedRule> rules, Context context) {
        Token token = rules.get(0).getToken();
        Object content = token.getContent();
        if (content instanceof Integer)
            return new Variable(VariableType.INT, token.getContent());
        else if (content instanceof String)
            return new Variable(VariableType.STRING, token.getContent());
        else if (content instanceof Boolean)
            return new Variable(VariableType.BOOL, token.getContent());
        throw new UnsupportedOperationException();
    }

    public static Object handleIdentifier(List<ConsumedRule> rules, Context context) {
        Token token = rules.get(0).getToken();
        return context.getVariable(token.getString(), token.getLineNumber());
    }

    public static Object forLoop(List<ConsumedRule> rules, Context context) {
        // Use 'in' and '..' as the line number sources
        int startLineNumber = rules.get(2).getToken().getLineNumber();
        int endLineNumber = rules.get(4).getToken().getLineNumber();
        String loopVariableName = rules.get(1).getToken().getString();
        Variable startVariable = rules.get(3).getVariable(context);
        Variable endVariable = rules.get(5).getVariable(context);
        if (!startVariable.getType().equals(VariableType.INT))
            throw new TypeException(startLineNumber, VariableType.INT, startVariable.getType());
        if (!endVariable.getType().equals(VariableType.INT))
            throw new TypeException(endLineNumber, VariableType.INT, endVariable.getType());
        Integer start = (Integer) startVariable.getValue();
        Integer end = (Integer) endVariable.getValue();

        Variable loopVariable = new Variable(loopVariableName, endLineNumber, VariableType.INT, start);
        context.updateVariable(loopVariable);

        for (Integer i = start; i <= end; i++) {
            context.pushFrame();

            rules.get(7).execute(context);

            context.popFrame();
            loopVariable = new Variable(loopVariableName, endLineNumber, VariableType.INT, i + 1);
            context.updateVariable(loopVariable);
        }

        return null;
    }

    public static Object handleNot(List<ConsumedRule> rules, Context context) {
        Variable variable = rules.get(1).getVariable(context);
        if (!variable.getType().equals(VariableType.BOOL)) {
            throw new OperationNotSupportedException(rules.get(0).getToken());
        }
        Boolean value = (Boolean) variable.getValue();
        return new Variable(VariableType.BOOL, !value);
    }

    public static Object assertExpression(List<ConsumedRule> rules, Context context) {
        int linenumber = rules.get(0).getToken().getLineNumber();
        Variable variable = rules.get(2).getVariable(context);
        if (!variable.getType().equals(VariableType.BOOL)) {
            throw new TypeException(linenumber, VariableType.BOOL, variable.getType());
        }
        Boolean value = (Boolean) variable.getValue();
        if (!value)
            throw new AssertionFailureException(linenumber);
        return null;
    }

    public static Object readVariable(List<ConsumedRule> rules, Context context) {
        int linenumber = rules.get(0).getToken().getLineNumber();
        String name = rules.get(1).getToken().getString();
        Variable variable = context.getVariable(name, linenumber);

        /* Why, MiniPL ;__;
        * read :: () -> Int
        * read :: () -> String
        */
        if (variable.getType().equals(VariableType.INT)) {
            String input = context.readLine();
            try {
                Integer value = Integer.parseInt(input);
                variable = new Variable(name, linenumber, VariableType.INT, value);
            } catch (NumberFormatException ignored) {
                throw new IntegerParseError(linenumber);
            }
        } else if (variable.getType().equals(VariableType.STRING)) {
            String input = context.readLine();
            variable = new Variable(name, linenumber, VariableType.STRING, input);
        } else {
            throw new UnsupportedInputException(linenumber);
        }

        context.updateVariable(variable);
        return null;
    }

    public static Object printExpression(List<ConsumedRule> rules, Context context) {
        Variable output = rules.get(1).getVariable(context);
        context.print(output.getValue());
        return null;
    }

    public static Object updateVariable(List<ConsumedRule> rules, Context context) {
        int linenumber = rules.get(0).getToken().getLineNumber();
        String name = rules.get(0).getToken().getString();
        Variable variable = context.getVariable(name, linenumber);
        Variable newValue = rules.get(2).getVariable(context);
        context.updateVariable(new Variable(name, variable.getLineNumber(), variable.getType(), newValue.getValue()));
        return null;
    }

    public static Object createVariable(List<ConsumedRule> rules, Context context) {
        int linenumber = rules.get(0).getToken().getLineNumber();
        String name = rules.get(1).getToken().getString();
        VariableType type = rules.get(3).getToken().getVariableType();
        List<ConsumedRule> value = rules.get(4).getList();
        if (!value.isEmpty()) {
            Variable variable = value.get(1).getVariable(context);
            if (!variable.getType().equals(type))
                throw new TypeException(linenumber, type, variable.getType());
            variable.setName(name);
            context.addVariable(variable);
        } else {
            context.addVariable(new Variable(name, linenumber, type));
        }
        return null;
    }

    public static Object handleOperation(List<ConsumedRule> rules, Context context) {
        Variable left = rules.get(0).getVariable(context);
        List<ConsumedRule> b = rules.get(1).getList();
        if (!b.isEmpty()) {
            Token operator = b.get(0).getToken();
            Variable right = b.get(1).getVariable(context);
            return handleOperation(left, operator, right);
        }
        return left;
    }

    private static Variable handleOperation(Variable left, Token operator, Variable right) {
        if (!left.getType().equals(right.getType())) {
            throw new TypeException(operator.getLineNumber(), left.getType(), right.getType());
        }

        if (left.getType().equals(VariableType.INT)) {
            return handleIntegerOperation(operator, (Integer) left.getValue(), (Integer) right.getValue());
        } else if (left.getType().equals(VariableType.STRING)) {
            return handleStringOperation(operator, (String) left.getValue(), (String) right.getValue());
        } else if (left.getType().equals(VariableType.BOOL)) {
            return handleBooleanOperation(operator, (Boolean) left.getValue(), (Boolean) right.getValue());
        }

        throw new OperationNotSupportedException(left.getType(), operator);
    }

    private static Variable handleIntegerOperation(Token operator, Integer left, Integer right) {
        TokenValue op = operator.getValue();
        if (op.equals(TokenValue.PLUS)) return new Variable(VariableType.INT, left + right);
        else if (op.equals(TokenValue.MINUS)) return new Variable(VariableType.INT, left - right);
        else if (op.equals(TokenValue.TIMES)) return new Variable(VariableType.INT, left * right);
        else if (op.equals(TokenValue.DIVIDE)) return new Variable(VariableType.INT, left / right);
        else if (op.equals(TokenValue.LESS_THAN)) return new Variable(VariableType.BOOL, left < right);
        else if (op.equals(TokenValue.EQUALS))
            return new Variable(VariableType.BOOL, left.equals(right));
        throw new OperationNotSupportedException(VariableType.INT, operator);
    }

    private static Variable handleStringOperation(Token operator, String left, String right) {
        TokenValue op = operator.getValue();
        if (op.equals(TokenValue.PLUS)) return new Variable(VariableType.STRING, left + right);
        else if (op.equals(TokenValue.EQUALS)) return new Variable(VariableType.BOOL, left.equals(right));
        throw new OperationNotSupportedException(VariableType.STRING, operator);
    }

    private static Variable handleBooleanOperation(Token operator, Boolean left, Boolean right) {
        TokenValue op = operator.getValue();
        if (op.equals(TokenValue.AND)) return new Variable(VariableType.BOOL, left && right);
        else if (op.equals(TokenValue.EQUALS)) return new Variable(VariableType.BOOL, left.equals(right));
        throw new OperationNotSupportedException(VariableType.BOOL, operator);
    }
}
