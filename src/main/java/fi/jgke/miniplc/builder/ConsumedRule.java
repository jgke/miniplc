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

import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.tokenizer.Token;

import java.util.List;

public class ConsumedRule {
    private final List<ConsumedRule> rules;
    private final Do getter;

    ConsumedRule(List<ConsumedRule> rules, Do getter) {
        this.rules = rules;
        this.getter = getter;
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(Context context, Class<T> type) {
        return type.cast(this.getter.handle(this.rules, context));
    }

    /**
     * Execute purely for side effects.
     */
    public void execute(Context context) {
        getValue(context, Object.class);
    }

    @SuppressWarnings("unchecked")
    public List<ConsumedRule> getList() {
        return getValue(null, List.class);
    }

    public Variable getVariable(Context context) {
        return getValue(context, Variable.class);
    }

    public Token getToken() {
        return getValue(null, Token.class);
    }

}
