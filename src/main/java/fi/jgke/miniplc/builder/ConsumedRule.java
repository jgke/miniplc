package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.interpreter.Context;

import java.util.List;

public class ConsumedRule {
    private List<ConsumedRule> rules;
    private Do getter;

    ConsumedRule(List<ConsumedRule> rules, Do getter) {
        this.rules = rules;
        this.getter = getter;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Context context, Class<T> type) {
        return type.cast(this.getter.handle(this.rules, context));
    }

    @SuppressWarnings("unchecked")
    <T> T getValue(Class<T> type) {
        return type.cast(this.getter.handle(this.rules, null));
    }

}
