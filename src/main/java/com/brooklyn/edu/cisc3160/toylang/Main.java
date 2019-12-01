package com.brooklyn.edu.cisc3160.toylang;

import com.brooklyn.edu.cisc3160.toylang.interpreter.Interpreter;

import java.io.*;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            String program;
            if (args.length < 2) {
                System.out.println("Info: Not enough arguments entered.\nexecuting built-in default test program...");
                // Default test program
                program = "x=1;\ny = 2;\nz = ---(x*y)*(x+-y);";
            } else {
                if (args[0].compareTo("--vars") == 0) {
                    // locate file
                    String fileName = args[1];
                    program = loadProgram(fileName);
                } else {
                    throw new Exception(String.format("Command unknown: '%s'", args[0]));
                }
            }

            Interpreter interpreter = new Interpreter();
            Map<String,Integer> varsTable = interpreter.getVarsTable(program);
            for (String var : varsTable.keySet()) {
                System.out.println(String.format("%s = %d", var, varsTable.get(var)));
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String loadProgram(String fileName) {
        StringBuilder sb = new StringBuilder();
        File file = new File(fileName + ".toy");

        try (BufferedReader reader = new BufferedReader(new FileReader(file));){
            String text = null;
            while ((text = reader.readLine()) != null) {
                sb.append(Integer.parseInt(text));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(sb.toString());
        return sb.toString();
    }
}
