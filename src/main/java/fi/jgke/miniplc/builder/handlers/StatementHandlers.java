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

public class StatementHandlers {
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

    public static Object updateVariable(List<ConsumedRule> rules, Context context) {
        int linenumber = rules.get(0).getToken().getLineNumber();
        String name = rules.get(0).getToken().getString();
        Variable variable = context.getVariable(name, linenumber);
        Variable newValue = rules.get(2).getVariable(context);
        context.updateVariable(new Variable(name, variable.getLineNumber(), variable.getType(), newValue.getValue()));
        return null;
    }

    public static Object printExpression(List<ConsumedRule> rules, Context context) {
        Variable output = rules.get(1).getVariable(context);
        context.print(output.getValue());
        return null;
    }

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
}
