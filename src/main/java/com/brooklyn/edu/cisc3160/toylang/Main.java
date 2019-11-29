package com.brooklyn.edu.cisc3160.toylang;

import com.brooklyn.edu.cisc3160.toylang.interpreter.Interpreter;

public class Main {
    public static void main(String[] args) {
        String[] programs = {
                //correct syntax
                "x = 3;",
                "x_2 = 10;",
                "x = 2 + 3;",
                "x = 2 * 3;",
                "x = -5;",
                "x = 3 - 8;",
                "x = 3;\ny = 5;\nz = x * 3 - y",
                "x = 3 * (5 - 5) + 2;",
                "l = 4;\nw = 4;\nA = (l * w);",
                "price = 5;\nqty = 12;\nbill = price * qty;",
                "x = 1;\ny = 2;\nz = ---(x*y)*(x+-y);",
                // incorrect syntax
                "x =;",
                "x = 7",
                "x = 003",
                "3x = 3;",
                "r = 5;",
                "A = s * s;",
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
