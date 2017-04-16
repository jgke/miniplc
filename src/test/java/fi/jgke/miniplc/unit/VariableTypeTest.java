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

import static fi.jgke.miniplc.interpreter.VariableType.*;

import fi.jgke.miniplc.interpreter.VariableType;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class VariableTypeTest {
    @Test
    public void noExtraTypes() throws Exception {
        assertEquals(values().length, 3);
        assertThat(VariableType.values(), hasItemInArray(INT));
        assertThat(VariableType.values(), hasItemInArray(STRING));
        assertThat(VariableType.values(), hasItemInArray(BOOL));
    }

    @Test
    public void parsesType() throws Exception {
        assertThat(VariableType.valueOf("INT"), equalTo(INT));
        assertThat(VariableType.valueOf("STRING"), equalTo(STRING));
        assertThat(VariableType.valueOf("BOOL"), equalTo(BOOL));
    }
}
