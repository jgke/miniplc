package fi.jgke.miniplc.samples;

import fi.jgke.miniplc.exception.*;
import fi.jgke.miniplc.interpreter.Executor;
import fi.jgke.miniplc.interpreter.InputOutput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class Errors {
    @Mock
    private
    InputOutput inputOutput;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = AssertionFailureException.class)
    public void assertion() {
        new Executor("assert(false);").execute(inputOutput);
    }

    @Test(expected = EndOfInputException.class)
    public void endOfInput() {
        new Executor(".").execute(inputOutput);
    }

    @Test(expected = IntegerParseError.class)
    public void parseError() {
        String sample = "var n : int; read n;";
        when(inputOutput.readLine()).thenReturn("foo");
        new Executor(sample).execute(inputOutput);
    }

    @Test(expected = OperationNotSupportedException.class)
    public void invalidOperation() {
        new Executor("var a : int := (5 & 5);").execute(inputOutput);
    }

    @Test(expected = TypeException.class)
    public void typeError() {
        new Executor("var a : int := true;").execute(inputOutput);
    }

    @Test(expected = UndefinedVariableException.class)
    public void undefinedVariable() {
        new Executor("a := 1;").execute(inputOutput);
    }

    @Test(expected = UnexpectedCharacterException.class)
    public void unexpectedCharacter() {
        new Executor(".1").execute(inputOutput);
    }

    @Test(expected = UnexpectedTokenException.class)
    public void unexpectedToken() {
        new Executor("var a : int;;").execute(inputOutput);
    }

    @Test(expected = UninitializedVariableException.class)
    public void uninitializedVariable() {
        new Executor("var a : int; print a;").execute(inputOutput);
    }

    @Test(expected = UnsupportedInputException.class)
    public void unsupportedInput() {
        String sample = "var n : bool; read n;";
        new Executor(sample).execute(inputOutput);
    }

    @Test(expected = VariableAlreadyDefinedException.class)
    public void duplicateVariables() {
        new Executor("var a : int; var a : int;").execute(inputOutput);
    }

    @Test(expected = UndefinedVariableException.class)
    public void variablesAreDestroyedWhenOutOfScope() {
        new Executor("" +
                "     var i : int; \n" +
                "     for i in 1..5 do \n" +
                "         var x : int := i;\n" +
                "     end for;\n" +
                "     print x;\n" +
                "").execute(inputOutput);
    }

    @Test
    public void outerVariablesCanBeUsed() {
        new Executor("" +
                "     var x : int := 5; \n" +
                "     var i : int; \n" +
                "     for i in 1..5 do \n " +
                "         print(x);\n" +
                "     end for;\n" +
                "     print i;\n" +
                "").execute(inputOutput);
    }
}
