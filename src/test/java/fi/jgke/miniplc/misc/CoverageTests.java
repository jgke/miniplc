package fi.jgke.miniplc.misc;

import fi.jgke.miniplc.Miniplc;
import fi.jgke.miniplc.builder.*;
import fi.jgke.miniplc.builder.handlers.ExpressionHandlers;
import fi.jgke.miniplc.builder.handlers.OperandHandlers;
import fi.jgke.miniplc.builder.handlers.StatementHandlers;
import fi.jgke.miniplc.builder.handlers.StatementsHandlers;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import fi.jgke.miniplc.tokenizer.Token;
import org.junit.Test;

import static fi.jgke.miniplc.tokenizer.TokenValue.DO;
import static fi.jgke.miniplc.tokenizer.TokenValue.MINUS;
import static fi.jgke.miniplc.tokenizer.TokenValue.SEMICOLON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/* Tests that purely increase code coverage */
public class CoverageTests {
    @Test
    public void constructorWorks() throws Exception {
        /* Increase code coverage, run dummy constructors */
        new BaseRules();
        new ExpressionHandlers();
        new OperandHandlers();
        new StatementHandlers();
        new StatementsHandlers();
        new Builder();
        new Syntax();
        new Miniplc();
    }

    @Test
    public void simpleConsumedRule() throws Exception {
        new SimpleConsumedRule("foo").toString();
    }

    @Test
    public void variable() {
        new Variable(VariableType.BOOL, true).toString();
    }

    @Test
    public void token() {
        assertThat(new Token(SEMICOLON).toString(), containsString("\n"));
        assertThat(new Token(DO).toString(), containsString("\n"));
        assertThat(new Token(MINUS).toString(), containsString("MINUS"));
    }

    @Test
    public void terminal() throws Exception {
        assertThat(Terminal.And.toString(), containsString("AND"));
    }
}
