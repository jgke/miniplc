package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.interpreter.Context;

import java.util.List;

@FunctionalInterface
interface Do {
    Object handle(List<ConsumedRule> rules, Context context);
}
