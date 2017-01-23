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

public class Variable {
    private String name;
    private final VariableType type;
    private final Object value;

    public Variable(VariableType type, Object value) {
        this.type = type;
        this.value = value;
        this.name = null;
        if (!(value == null || value instanceof Integer || value instanceof String || value instanceof Boolean))
            throw new IllegalStateException();

    }

    public Variable(String name, VariableType type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
        if (!(value == null || value instanceof Integer || value instanceof String || value instanceof Boolean))
            throw new IllegalStateException();
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

    public Object getNullableValue() throws UninitializedVariableException {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
