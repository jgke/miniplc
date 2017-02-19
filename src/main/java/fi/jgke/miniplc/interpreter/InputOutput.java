package fi.jgke.miniplc.interpreter;

import java.util.Scanner;

public class InputOutput {
    private static Scanner input = new Scanner(System.in);
    private static InputOutput instance;

    private InputOutput() {
    }

    public static InputOutput getInstance() {
        if(InputOutput.instance == null)
            InputOutput.instance = new InputOutput();
        return InputOutput.instance;
    }

    public String readLine() {
        return input.nextLine();
    }

    public void print(Object output) {
        System.out.print(output);
    }
}
