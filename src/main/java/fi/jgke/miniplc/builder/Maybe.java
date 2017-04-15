package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.tokenizer.TokenQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class Maybe implements Rule {
    List<Rule> rules;

    private Maybe(Rule... rules) {
        this.rules = Arrays.asList(rules);
    }

    public static Maybe maybe(Rule... matchables) {
        return new Maybe(matchables);
    }

    @Override
    public boolean matches(TokenQueue tokenQueue) {
        return true;
    }

    public ConsumedRule consume(TokenQueue queue) {
        if(this.rules.get(0).matches(queue))
            return new SimpleConsumedRule(rules.stream().map(r -> r.consume(queue)).collect(Collectors.toList()));
        return new SimpleConsumedRule(new ArrayList<>());
    }

    public String str() {
        return "Maybe{" +
                "rules=" + rules.stream().map(Rule::str).collect(Collectors.toList()) +
                '}';
    }
}
