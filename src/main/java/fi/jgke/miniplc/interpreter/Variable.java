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
package fi.jgke.miniplc.interpreter;

import fi.jgke.miniplc.exception.TypeException;
import fi.jgke.miniplc.exception.UninitializedVariableException;

public class Variable {
    private final String name;
    private final VariableType type;
    private final Object value;
    private final int lineNumber;

    private boolean typeMatches(VariableType type, Object value) {
        return (type.equals(VariableType.INT) && value instanceof Integer)
                || (type.equals(VariableType.STRING) && value instanceof String)
                || (type.equals(VariableType.BOOL) && value instanceof Boolean);
    }

    public Variable(VariableType type, Object value) {
        this.lineNumber = 0;
        this.name = null;
        this.type = type;
        this.value = value;
        checkType(type, value);
    }

    private void checkType(VariableType type, Object value) {
        if (value == null || !typeMatches(type, value)) {
            throw new TypeException(this.lineNumber, type, VariableType.getValueForObject(value));
        }
    }

    public Variable(String name, int lineNumber, VariableType type) {
        this.lineNumber = lineNumber;
        this.name = name;
        this.type = type;
        this.value = null;
    }

    public Variable(String name, int lineNumber, VariableType type, Object value) {
        this.lineNumber = lineNumber;
        this.name = name;
        this.type = type;
        this.value = value;
        checkType(type, value);
    }

    public String getName() {
        return name;
    }

    public VariableType getType() {
        return type;
    }

    public Object getValue() throws UninitializedVariableException {
        if (value == null) {
            throw new UninitializedVariableException(this);
        }

        return value;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", value=" + value +
                '}';
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
