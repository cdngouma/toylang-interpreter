package com.brooklyn.edu.cisc3160.toylang.interpreter;

import java.util.List;

public class Interpreter {

    public static void run(String program) {
        Parser parser = new Parser();
        List<String> tokens = SyntaxScanner.scan(program);
        System.out.println(tokens);

        boolean isValid = parser.parse(tokens);
        System.out.println(isValid);
    }
}
