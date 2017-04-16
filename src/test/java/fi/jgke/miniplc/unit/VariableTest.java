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
import fi.jgke.miniplc.exception.UndefinedVariableException;
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.InputOutput;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class VariableTest {

    private void createVariable(VariableType type, Object value) {
        new Variable(type, value);
    }

    @Test
    public void typeSanity() {
        List<Pair<VariableType, Object>> types = new ArrayList<>();
        types.add(new ImmutablePair<>(VariableType.BOOL, "str"));
        types.add(new ImmutablePair<>(VariableType.BOOL, 5));
        types.add(new ImmutablePair<>(VariableType.BOOL, null));
        types.add(new ImmutablePair<>(VariableType.STRING, true));
        types.add(new ImmutablePair<>(VariableType.STRING, 5));
        types.add(new ImmutablePair<>(VariableType.STRING, null));
        types.add(new ImmutablePair<>(VariableType.INT, true));
        types.add(new ImmutablePair<>(VariableType.INT, "str"));
        types.add(new ImmutablePair<>(VariableType.INT, null));
        for(Pair<VariableType, Object> p : types) {
            try {
                createVariable(p.getLeft(), p.getRight());
                assertTrue(false);
            } catch (TypeException e) {
                assertNotNull(p.getRight());
            } catch (IllegalStateException e) {
                assertNull(p.getRight());
            }
        }
    }

    @Test
    public void validTypes() {
        createVariable(VariableType.BOOL, true);
        createVariable(VariableType.INT, 5);
        createVariable(VariableType.STRING, "foo");
    }

    @Test(expected = UndefinedVariableException.class)
    public void updateNonexistentVariableToContext() throws Exception {
        Variable a = new Variable("foo", 1, VariableType.BOOL, true);
        Context context = new Context(InputOutput.getInstance());
        context.updateVariable(a);
    }
}