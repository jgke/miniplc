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
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import fi.jgke.miniplc.tokenizer.Token;

import java.util.List;

public class OperandHandlers {

    /* Get a constant such as 5, true or "foo" */
    public static Object handleConstant(List<ConsumedRule> rules, Context context) {
        Token token = rules.get(0).getToken();
        Object content = token.getContent();
        if (content instanceof Integer)
            return new Variable(VariableType.INT, token.getContent());
        else if (content instanceof String)
            return new Variable(VariableType.STRING, token.getContent());
        else
            return new Variable(VariableType.BOOL, token.getContent());
    }

    /* Get any lone variable */
    public static Object handleIdentifier(List<ConsumedRule> rules, Context context) {
        Token token = rules.get(0).getToken();
        return context.getVariable(token.getString(), token.getLineNumber());
    }

    /* handle (expression) */
    public static Object handleExpression(List<ConsumedRule> rules, Context context) {
        return rules.get(1).getVariable(context);
    }
}
