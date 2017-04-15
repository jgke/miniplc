package fi.jgke.miniplc.unit;

import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import org.junit.Test;

public class ToStrings {
    @Test
    public void variable() {
        System.out.println(new Variable(VariableType.BOOL, true));
    }
}
