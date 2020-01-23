package com.brooklyn.edu.cisc3160.toylang;

import com.brooklyn.edu.cisc3160.toylang.interpreter.Interpreter;

import java.io.*;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length >= 2) {
                String path = args[1];

                if (args[0].compareTo("-vars") == 0 || args[0].compareTo("--variables") == 0) {
                    String srcCode = extractSourceCode(path);
                    Interpreter interpreter = new Interpreter();
                    Map<String, Integer> varsTable = interpreter.getVarsTable(srcCode);

                    System.out.println("printing variables...");
                    for (String var : varsTable.keySet()) {
                        System.out.println(String.format("%s = %d", var, varsTable.get(var)));
                    }
                } else {
                    throw new Exception(String.format("Command unknown: '%s'.", args[0]));
                }
            } else {
                throw new Exception("Not enough arguments");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Locates the file and return its content as a String.
     * @param path The path to the file.
     * @return String representation of the source code.
     * @throws Exception When a supported file could not be found.
     */
    private static String extractSourceCode(String path) throws Exception {
        if (!Pattern.matches("^\\S+\\.toy$", path)) throw new Exception("File not supported");
        File file = new File(path);

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
            String text = null;
            while ((text = reader.readLine()) != null) {
                sb.append(text);
            }
            return sb.toString();
        }
    }
}
