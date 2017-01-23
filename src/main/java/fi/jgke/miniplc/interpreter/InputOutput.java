package fi.jgke.miniplc.interpreter;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

public class InputOutput {
    private static Scanner input = new Scanner(System.in);
    private static Queue<String> queue = new ArrayDeque<>();

    public static String readLine() {
        if(!queue.isEmpty())
            return queue.remove();
        return input.nextLine();
    }

    public static void print(Object output) {
        System.out.print(output);
    }

    public static void addNextLine(String s) {
        queue.add(s);
    }
}
