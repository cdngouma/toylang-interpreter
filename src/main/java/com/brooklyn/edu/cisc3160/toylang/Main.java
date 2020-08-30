package com.brooklyn.edu.cisc3160.toylang;

import com.brooklyn.edu.cisc3160.toylang.interpreter.Interpreter;
import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UnexpectedTokenException;
import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UninitializedVariableException;

import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                final String arg = args[0];
                if (arg.compareTo("-vars") == 0 || arg.compareTo("--vars-table") == 0) {
                    String srcCode = "";
                    if (args.length == 2) {
                        final String path = args[1];
                        srcCode = getSourceCodeFromFile(path);
                    } else {
                        srcCode = writeSourceCode();
                    }
                    printVarsTable(srcCode);
                } else {
                    throw new Exception(String.format("Command unknown: '%s'.", args[0].replaceFirst("^(-)+", "")));
                }
            } else {
                System.out.println("No arguments entered\nRunning default script...\ntest.toy");
                String srcCode = "x = 1;\n" +
                        "y = 2;\n" +
                        "z = ---(x * y) * (x + -y);";
                System.out.println(srcCode);
                printVarsTable(srcCode);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void printVarsTable(String srcCode) throws UnexpectedTokenException, UninitializedVariableException {
        Interpreter interpreter = new Interpreter();
        Map<String, Integer> varsTable = interpreter.getVarsTable(srcCode);
        // Print variables and their values
        System.out.println();
        System.out.println("printing variables table...");
        for (String var : varsTable.keySet()) {
            System.out.println(String.format("%s : %d", var, varsTable.get(var)));
        }
    }

    /**
     * Register user input as source code.
     * @return String representation of the source code.
     */
    private static String writeSourceCode() {
        Scanner scanner = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        String line = "";
        System.out.println("Type '$END' then press <ENTER> to finish");
        line = scanner.nextLine();
        while (line.compareTo("$END") != 0) {
            sb.append(line);
            line = scanner.nextLine();
        }
        return sb.toString();
    }

    /**
     * Locates the file and return its content as a String.
     * @param path The path to the file.
     * @return String representation of the source code.
     * @throws Exception When a supported file could not be found.
     */
    private static String getSourceCodeFromFile(String path) throws Exception {
        if (!Pattern.matches("^\\S+\\.toy$", path)) throw new Exception("File not supported");
        File file = new File(path);

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
            String text;
            while ((text = reader.readLine()) != null) {
                sb.append(text);
            }
            return sb.toString();
        }
    }
}
