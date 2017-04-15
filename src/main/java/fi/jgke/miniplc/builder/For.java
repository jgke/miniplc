package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.tokenizer.TokenQueue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class For implements Rule {
    private List<Rule> rules;

    private For(Rule ...rules) {
        this.rules = Arrays.asList(rules);
    }

    public boolean matches(TokenQueue queue) {
        return rules.get(0).matches(queue);
    }

    public static For when(Rule ...rules) {
        return new For(rules);
    }

    public SimpleConsumedRule consume(TokenQueue tokenQueue) {
        List<ConsumedRule> tokens = this.rules.stream()
                .map(rule -> rule.consume(tokenQueue))
                .collect(Collectors.toList());
        return new SimpleConsumedRule(tokens);
    }

    public String str() {
        return "For{" +
                "rules=" + rules.stream().map(Rule::str).collect(Collectors.toList()) +
                '}';
    }
}
