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

public class SimpleConsumedRule extends ConsumedRule {
    private final Object content;

    public SimpleConsumedRule(Object content) {
        super(null, (ignored, parameters) -> content);
        this.content = content;
    }

    @Override
    public String toString() {
        return "SimpleConsumedRule{" +
                "content=" + (content instanceof Terminal ? (Terminal) content : content) +
                '}';
    }
}
