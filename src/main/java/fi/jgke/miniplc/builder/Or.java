package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.tokenizer.TokenQueue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Or implements Rule {

    private List<Rule> rules;

    private Or(List<Rule> rules) {
        this.rules = rules;
    }

    public static Or or(Rule... rules) {
        return new Or(Arrays.asList(rules));
    }

    @Override
    public boolean matches(TokenQueue tokenQueue) {
        return rules.stream().anyMatch(rule -> rule.matches(tokenQueue));
    }

    @Override
    public ConsumedRule consume(TokenQueue tokenQueue) {
        for(Rule rule : rules) {
            if(rule.matches(tokenQueue))
                return rule.consume(tokenQueue);
        }
        throw new RuleNotMatchedException();
    }

    public String str() {
        return "Or{" +
                "rules=" + rules.stream().map(rule -> rule.str() + "\n").collect(Collectors.toList()) +
                '}';
    }
}
