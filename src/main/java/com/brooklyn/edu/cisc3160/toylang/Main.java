package com.brooklyn.edu.cisc3160.toylang;

import com.brooklyn.edu.cisc3160.toylang.interpreter.Interpreter;

public class Main {
    public static void main(String[] args) {
        String[] programs = {
                //correct syntax
                "x = 3;",
                "x = 2 + 3;",
                "x = 3;\ny = 5 - x;",
                "x = 1;\ny = 2;\nz= ---(x + y);",
                "A = 2 * (l + w);",
                "price = 5;\nqty = 12;\nbill = price * qty;",
                // incorrect syntax
                "x = 3",
                "x 3",
                "9r = 87;",
                "x = 003;",
                "8 = 5;",
                "if == 98;"
        };

        int len = programs.length;

        for (String program : programs) {
            Interpreter.run(program);
            System.out.println();
        }
    }
}
