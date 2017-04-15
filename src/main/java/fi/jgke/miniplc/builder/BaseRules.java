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

import fi.jgke.miniplc.tokenizer.TokenQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BaseRules {
    public static Rule empty() {
        return new Rule() {
            @Override
            public boolean matches(TokenQueue tokenQueue) {
                return true;
            }

            @Override
            public ConsumedRule consume(TokenQueue tokenQueue) {
                return new SimpleConsumedRule(new ArrayList<>());
            }
        };
    }

    public static Rule all(Rule... rules) {
        return new Rule() {
            public boolean matches(TokenQueue queue) {
                return rules[0].matches(queue);
            }

            public SimpleConsumedRule consume(TokenQueue tokenQueue) {
                List<ConsumedRule> tokens = Arrays.stream(rules)
                        .map(rule -> rule.consume(tokenQueue))
                        .collect(Collectors.toList());
                return new SimpleConsumedRule(tokens);
            }
        };
    }

    public static Rule maybe(Rule... rules) {
        return new Rule() {
            @Override
            public boolean matches(TokenQueue tokenQueue) {
                return true;
            }

            public ConsumedRule consume(TokenQueue queue) {
                if (rules[0].matches(queue))
                    return new SimpleConsumedRule(Arrays.stream(rules)
                            .map(r -> r.consume(queue))
                            .collect(Collectors.toList()));
                return new SimpleConsumedRule(new ArrayList<>());
            }
        };
    }

    public static Rule any(Rule... rules) {
        return new Rule() {
            @Override
            public boolean matches(TokenQueue tokenQueue) {
                return Arrays.stream(rules).anyMatch(rule -> rule.matches(tokenQueue));
            }

            @Override
            public ConsumedRule consume(TokenQueue tokenQueue) {
                for (Rule rule : rules) {
                    if (rule.matches(tokenQueue))
                        return rule.consume(tokenQueue);
                }
                throw new RuleNotMatchedException(tokenQueue.element().getLineNumber());
            }
        };
    }

    public static Rule lazy(Supplier<Rule> provider) {
        return new Rule() {
            @Override
            public boolean matches(TokenQueue tokenQueue) {
                return provider.get().matches(tokenQueue);
            }

            @Override
            public ConsumedRule consume(TokenQueue tokenQueue) {
                return provider.get().consume(tokenQueue);
            }
        };
    }

    public static Rule rule(Rule when, Do something) {
        return new Rule() {
            @Override
            public boolean matches(TokenQueue tokenQueue) {
                return when.matches(tokenQueue);
            }

            @Override
            public ConsumedRule consume(TokenQueue tokenQueue) {
                return new ConsumedRule(when.consume(tokenQueue).getList(), something);
            }
        };
    }
}
