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
import fi.jgke.miniplc.exception.AssertionFailureException;
import fi.jgke.miniplc.exception.IntegerParseError;
import fi.jgke.miniplc.exception.TypeException;
import fi.jgke.miniplc.exception.UnsupportedInputException;
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;

import java.util.List;
import java.util.stream.IntStream;

public class StatementHandlers {
    /**
     * Create a variable with a name, type and an optional value, and add it to the execution context
     */
    public static Object createVariable(List<ConsumedRule> rules, Context context) {
        int linenumber = rules.get(0).getToken().getLineNumber();
        String name = rules.get(1).getToken().getString();
        VariableType type = rules.get(3).getToken().getVariableType();
        List<ConsumedRule> value = rules.get(4).getList();

        Variable variable;
        if (!value.isEmpty()) {
            variable = new Variable(name, linenumber, type, value.get(1).getVariable(context).getValue());
        } else {
            variable = new Variable(name, linenumber, type);
        }
        context.addVariable(variable);

        return null;
    }

    /**
     * Set a new value to a variable, and update it to the execution context
     */
    public static Object updateVariable(List<ConsumedRule> rules, Context context) {
        int linenumber = rules.get(0).getToken().getLineNumber();
        String name = rules.get(0).getToken().getString();
        Variable variable = context.getVariable(name, linenumber);
        Variable newValue = rules.get(2).getVariable(context);
        context.updateVariable(new Variable(name, variable.getLineNumber(), variable.getType(), newValue.getValue()));
        return null;
    }

    /**
     * Print an expression's value to the standard output
     */
    public static Object printExpression(List<ConsumedRule> rules, Context context) {
        Variable output = rules.get(1).getVariable(context);
        context.print(output.getValue());
        return null;
    }

    /**
     * Read a variable from the standard input, and add it to the execution context
     */
    public static Object readVariable(List<ConsumedRule> rules, Context context) {
        int linenumber = rules.get(0).getToken().getLineNumber();
        String name = rules.get(1).getToken().getString();
        Variable variable = context.getVariable(name, linenumber);

        /* Why, MiniPL ;__;
        * read :: () -> Int & String
        */
        if (variable.getType().equals(VariableType.INT)) {
            variable = readIntegerVariable(context, linenumber, name);
        } else if (variable.getType().equals(VariableType.STRING)) {
            variable = readStringVariable(context, linenumber, name);
        } else {
            throw new UnsupportedInputException(linenumber);
        }

        context.updateVariable(variable);
        return null;
    }

    private static Variable readIntegerVariable(Context context, int linenumber, String name) {
        Variable variable;
        String input = context.readLine();
        try {
            Integer value = Integer.parseInt(input);
            variable = new Variable(name, linenumber, VariableType.INT, value);
        } catch (NumberFormatException ignored) {
            throw new IntegerParseError(linenumber);
        }
        return variable;
    }

    private static Variable readStringVariable(Context context, int linenumber, String name) {
        Variable variable;
        String input = context.readLine();
        variable = new Variable(name, linenumber, VariableType.STRING, input);
        return variable;
    }

    /* Assert that the expression is true */
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

    /* Run a for loop */
    public static Object forLoop(List<ConsumedRule> rules, Context context) {
        int loopVariableLineNumber = rules.get(2).getToken().getLineNumber();
        String loopVariableName = rules.get(1).getToken().getString();
        Integer start = getLoopRangeLimit(rules, context, 3);
        Integer end = getLoopRangeLimit(rules, context, 5);

        ConsumedRule loopBody = rules.get(7);

        executeLoopBody(context, loopVariableLineNumber, loopVariableName, start, end, loopBody);

        return null;
    }

    private static Integer getLoopRangeLimit(List<ConsumedRule> rules, Context context, int index) {
        Variable startVariable = rules.get(index).getVariable(context);
        if (!startVariable.getType().equals(VariableType.INT))
            throw new TypeException(rules.get(index - 1).getToken().getLineNumber(), VariableType.INT, startVariable.getType());
        return (Integer) startVariable.getValue();
    }

    private static void executeLoopBody(Context context, int endLineNumber, String loopVariableName, Integer start, Integer end, ConsumedRule loopBody) {
        IntStream.range(start, end + 1).forEach(i -> {
            /* Push a frame, so that the inner variables don't leak */
            context.pushFrame();
            Variable loopVariable = new Variable(loopVariableName, endLineNumber, VariableType.INT, i);
            context.updateVariable(loopVariable);

            loopBody.execute(context);

            /* and pop the frame to remove the variables from the context */
            context.popFrame();
        });
        // Because specification's for loop example - leak the loop variable
        context.updateVariable(new Variable(loopVariableName, endLineNumber, VariableType.INT, end + 1));

    }
}
