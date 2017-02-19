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
import fi.jgke.miniplc.exception.OperationNotSupportedException;
import fi.jgke.miniplc.exception.TypeException;
import fi.jgke.miniplc.interpreter.*;
import fi.jgke.miniplc.exception.RuntimeException;

import java.util.Optional;

public class Expression implements ExecutableWithResult {

    private Optional<Token> operator;
    private ExecutableWithResult leftOperand;
    private Optional<ExecutableWithResult> rightOperand;

    @Override
    public void parse(TokenQueue tokens) throws RuntimeException {
        Token operator;
        if (tokens.peek().getValue().equals(TokenValue.NOT)) {
            operator = tokens.getExpectedToken(TokenValue.NOT);
            leftOperand = new Operand();
            leftOperand.parse(tokens);
        } else {
            leftOperand = new Operand();
            leftOperand.parse(tokens);

            if (!tokens.peek().getValue().isOperator()) {
                operator = null;
            } else {
                operator = tokens.remove();
                if (operator.getValue().equals(TokenValue.NOT)) {
                    throw new OperationNotSupportedException(TokenValue.NOT);
                }

                ExecutableWithResult rightOperand = new Operand();
                rightOperand.parse(tokens);

                this.rightOperand = Optional.of(rightOperand);
            }
        }

        this.operator = Optional.ofNullable(operator);
    }

    @Override
    public Variable execute(Context context) throws RuntimeException {
        if (!this.operator.isPresent()) {
            return this.leftOperand.execute(context);
        }
        Token operator = this.operator.get();
        TokenValue op = operator.getValue();

        if (op.equals(TokenValue.NOT)) {
            Boolean result = (Boolean) leftOperand.execute(context).getValue();
            return new Variable(VariableType.BOOL, !result);
        }

        Variable left = leftOperand.execute(context);
        Variable right = rightOperand.get().execute(context);

        if (!left.getType().equals(right.getType())) {
            throw new TypeException(left.getType(), right.getType());
        }

        /* Booleans only support AND and EQUALS */
        if (left.getType().equals(VariableType.BOOL) && !(op.equals(TokenValue.AND) || op.equals(TokenValue.EQUALS))) {
            throw new OperationNotSupportedException(left.getType(), operator);
        }

        /* Strings only support PLUS and EQUALS */
        if (left.getType().equals(VariableType.STRING) && !(op.equals(TokenValue.PLUS) || op.equals(TokenValue.EQUALS))) {
            throw new OperationNotSupportedException(left.getType(), operator);
        }

        /* Cannot use AND with numbers */
        if (left.getType().equals(VariableType.INT) && op.equals(TokenValue.AND)) {
            throw new OperationNotSupportedException(left.getType(), operator);
        }

        Variable result;
        switch (left.getType()) {
            case INT: {
                Integer lval = (Integer) left.getValue();
                Integer rval = (Integer) right.getValue();
                switch (op) {
                    case PLUS:
                        result = new Variable(VariableType.INT, lval + rval);
                        break;
                    case MINUS:
                        result = new Variable(VariableType.INT, lval - rval);
                        break;
                    case TIMES:
                        result = new Variable(VariableType.INT, lval * rval);
                        break;
                    case DIVIDE:
                        result = new Variable(VariableType.INT, lval / rval);
                        break;
                    case LESSTHAN:
                        result = new Variable(VariableType.BOOL, lval < rval);
                        break;
                    case EQUALS:
                        result = new Variable(VariableType.BOOL, lval == rval);
                        break;
                    default:
                        throw new OperationNotSupportedException(left.getType(), operator);
                }
                break;
            }
            case STRING: {
                String lval = (String) left.getValue();
                String rval = (String) right.getValue();
                switch (op) {
                    case PLUS:
                        result = new Variable(VariableType.STRING, lval + rval);
                        break;
                    case EQUALS:
                        result = new Variable(VariableType.BOOL, lval.equals(rval));
                        break;
                    default:
                        throw new OperationNotSupportedException(left.getType(), operator);
                }
                break;
            }
            case BOOL: {
                Boolean lval = (Boolean) left.getValue();
                Boolean rval = (Boolean) right.getValue();
                switch (op) {
                    case AND:
                        result = new Variable(VariableType.BOOL, lval && rval);
                        break;
                    case EQUALS:
                        result = new Variable(VariableType.BOOL, lval.equals(rval));
                        break;
                    default:
                        throw new OperationNotSupportedException(left.getType(), operator);
                }
                break;
            }
            default:
                throw new OperationNotSupportedException(left.getType(), operator);
        }
        result.getValue();
        return result;
    }
}
