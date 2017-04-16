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

package fi.jgke.miniplc.builder.handlers;

import fi.jgke.miniplc.builder.ConsumedRule;
import fi.jgke.miniplc.exception.OperationNotSupportedException;
import fi.jgke.miniplc.exception.TypeException;
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import fi.jgke.miniplc.tokenizer.Token;
import fi.jgke.miniplc.tokenizer.TokenValue;

import java.util.List;

public class ExpressionHandlers {
    public static Object handleNot(List<ConsumedRule> rules, Context context) {
        Variable variable = rules.get(1).getVariable(context);
        if (!variable.getType().equals(VariableType.BOOL)) {
            throw new OperationNotSupportedException(rules.get(0).getToken());
        }
        Boolean value = (Boolean) variable.getValue();
        return new Variable(VariableType.BOOL, !value);
    }

    public static Object handleOperation(List<ConsumedRule> rules, Context context) {
        Variable left = rules.get(0).getVariable(context);
        List<ConsumedRule> b = rules.get(1).getList();
        if (!b.isEmpty()) {
            Token operator = b.get(0).getToken();
            Variable right = b.get(1).getVariable(context);
            return handleBinaryOperation(left, operator, right);
        }
        return left;
    }

    private static Object handleBinaryOperation(Variable left, Token operator, Variable right) {
        if (!left.getType().equals(right.getType())) {
            throw new TypeException(operator.getLineNumber(), left.getType(), right.getType());
        }

        if (left.getType().equals(VariableType.INT)) {
            return handleIntegerOperation(operator, (Integer) left.getValue(), (Integer) right.getValue());
        } else if (left.getType().equals(VariableType.STRING)) {
            return handleStringOperation(operator, (String) left.getValue(), (String) right.getValue());
        } else {
            return handleBooleanOperation(operator, (Boolean) left.getValue(), (Boolean) right.getValue());
        }
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
