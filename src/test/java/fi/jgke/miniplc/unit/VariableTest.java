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

package fi.jgke.miniplc.unit;

import fi.jgke.miniplc.exception.TypeException;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class VariableTest {

    private Variable createVariable(VariableType type, Object value) {
        return new Variable(type, value);
    }

    @Test
    public void typeSanity() {
        Map<VariableType, Object> types = new HashMap<>();
        types.put(VariableType.BOOL, "str");
        types.put(VariableType.BOOL, 5);
        types.put(VariableType.BOOL, null);
        types.put(VariableType.STRING, true);
        types.put(VariableType.STRING, 5);
        types.put(VariableType.STRING, null);
        types.put(VariableType.INT, true);
        types.put(VariableType.INT, "str");
        types.put(VariableType.INT, null);
        for(VariableType type : types.keySet()) {
            try {
                createVariable(type, types.get(type));
                assertTrue(false);
            } catch (IllegalStateException e) {
            }
        }
    }
}