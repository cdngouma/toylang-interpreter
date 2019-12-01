package com.brooklyn.edu.cisc3160.toylang;

import com.brooklyn.edu.cisc3160.toylang.interpreter.Interpreter;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        try {
            String fileName;
            String program;
            if (args.length < 2) {
                System.out.println("Info: Not enough arguments entered.\nloading default test program...");
                fileName = "test.toy";
                ClassLoader classLoader = Main.class.getClassLoader();
                File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
                program = loadProgram(file);
            } else if (args[0].compareTo("--vars") == 0) {
                fileName = args[1];
                if (!Pattern.matches(".toy$", fileName)) {
                    throw new Exception("Error: unsupported file format.");
                } else {
                    // TODO: Implement functionality to load files
                    throw new Exception("Info: External files not supported yet.");
                }
            } else {
                throw new Exception(String.format("Command unknown: '%s'.", args[0]));
            }

            Interpreter interpreter = new Interpreter();
            Map<String, Integer> varsTable = interpreter.getVarsTable(program);

            for (String var : varsTable.keySet()) {
                System.out.println(String.format("%s = %d", var, varsTable.get(var)));
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String loadProgram(File file) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
            String text = null;
            while ((text = reader.readLine()) != null) {
                sb.append(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
