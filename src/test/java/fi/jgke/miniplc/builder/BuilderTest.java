package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.InputOutput;
import fi.jgke.miniplc.tokenizer.TokenQueue;
import org.junit.Test;

public class BuilderTest {

    @Test
    public void parsesStatement() {
        Context context = new Context(InputOutput.getInstance());
        TokenQueue tokenQueue = new TokenQueue("var foo : int := 5 + 5;");

        Builder.parseAndExecute(tokenQueue, context);
    }
}