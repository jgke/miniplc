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
package fi.jgke.miniplc.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Stack {
    ArrayList<Map<String, Variable>> variables;

    public Stack() {
        this.variables = new ArrayList<>();
        this.variables.add(new HashMap<>());
    }

    public void addVariable(Variable variable) throws VariableAlreadyDefinedException {
        if (variables.get(variables.size() - 1).containsKey(variable.getName())) {
            throw new VariableAlreadyDefinedException(variable.getName());
        }
        variables.get(variables.size() - 1).put(variable.getName(), variable);
    }

    public void updateVariable(Variable variable) throws UndefinedVariableException {
        String name = variable.getName();
        Map<String, Variable> map;
        for (int i = variables.size() - 1; i >= 0; i--) {
            map = variables.get(i);
            if (map.containsKey(name)) {
                map.put(name, variable);
                return;
            }
        }
        throw new UndefinedVariableException(name);
    }

    public Variable getVariable(String name) throws UndefinedVariableException {
        Map<String, Variable> map;
        for (int i = variables.size() - 1; i >= 0; i--) {
            map = variables.get(i);
            if (map.containsKey(name))
                return map.get(name);
        }
        throw new UndefinedVariableException(name);
    }

    public void pushFrame() {
        variables.add(new HashMap<>());
    }

    public void popFrame() {
        variables.remove(variables.size() - 1);
    }
}