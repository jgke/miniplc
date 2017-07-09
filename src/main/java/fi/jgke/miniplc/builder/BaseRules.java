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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BaseRules {
    public static Rule empty() {
        return new Rule() {
            @Override
            public boolean matches() {
                return true;
            }

            @Override
            public ConsumedRule consume() {
                return new SimpleConsumedRule(new ArrayList<>());
            }
        };
    }

    public static Rule all(Rule... rules) {
        return new Rule() {
            public boolean matches() {
                return rules[0].with(tokenQueue).matches();
            }

            public SimpleConsumedRule consume() {
                List<ConsumedRule> tokens = Arrays.stream(rules)
                        .map(rule -> rule.with(tokenQueue).consume())
                        .collect(Collectors.toList());
                return new SimpleConsumedRule(tokens);
            }
        };
    }

    public static Rule any(Rule... rules) {
        return new Rule() {
            @Override
            public boolean matches() {
                return Arrays.stream(rules).anyMatch(rule -> rule.with(tokenQueue).matches());
            }

            @Override
            public ConsumedRule consume() {
                return Arrays.stream(rules)
                        .map(rule -> rule.with(tokenQueue))
                        .filter(Rule::matches)
                        .findFirst()
                        .map(Rule::consume)
                        .orElseThrow(() ->
                                new RuleNotMatchedException(tokenQueue.element().getLineNumber())
                        );
            }
        };
    }

    public static Rule lazy(Supplier<Rule> provider) {
        return new Rule() {
            @Override
            public boolean matches() {
                return provider.get().with(tokenQueue).matches();
            }

            @Override
            public ConsumedRule consume() {
                return provider.get().with(tokenQueue).consume();
            }
        };
    }

    public static Rule rule(Do something, Rule ...when) {
        return new Rule() {
            @Override
            public boolean matches() {
                return all(when).with(tokenQueue).matches();
            }

            @Override
            public ConsumedRule consume() {
                return new ConsumedRule(all(when).with(tokenQueue).consume().getList(), something);
            }
        };
    }
}
