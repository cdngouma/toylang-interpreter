package com.brooklyn.edu.cisc3160.toylang.interpreter;

import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UnexpectedTokenException;
import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UninitializedVariableException;
import com.brooklyn.edu.cisc3160.toylang.interpreter.util.ASTNode;
import com.brooklyn.edu.cisc3160.toylang.interpreter.util.ParseTree;
import com.brooklyn.edu.cisc3160.toylang.interpreter.util.Parser;
import com.brooklyn.edu.cisc3160.toylang.interpreter.util.Tokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Interpreter {

    public static void run(String program) {
        Parser parser = new Parser();
        List<String> tokens = Tokenizer.tokenize(program);
        System.out.println(tokens);

        ParseTree parseTree = null;
        try {
            parseTree = parser.parse(tokens);
            for (ASTNode node : parseTree.getChildren()) {
                printTree(node);
                System.out.println();
            }

        /*    Map<String,Integer> varsTable = buildVarsTable(parseTree);

            System.out.println("VARS TABLE:");
            for (String var : varsTable.keySet()) {
                System.out.println(String.format("%s = %d", var, varsTable.get(var)));
            }*/
        } catch (UnexpectedTokenException | UninitializedVariableException e) {
            System.err.println(e.getMessage());
        }
    }

    private static Map<String,Integer> buildVarsTable(ParseTree parseTree) throws UninitializedVariableException {
        Map<String,Integer> varsTable = new HashMap<>();
        for (ASTNode node : parseTree.getChildren()) {
            String var = node.getLeft().getValue();
            int value = evaluateAST(node.getRight(), varsTable);
            varsTable.put(var, value);
        }
        return varsTable;
    }

    private static int evaluateAST(ASTNode node, Map<String, Integer> varsTable) throws UninitializedVariableException {
        if (node == null) return 1;

        int leftVal = evaluateAST(node.getLeft(), varsTable);
        int rightVal = evaluateAST(node.getRight(), varsTable);

        String value = node.getValue();

        switch (value) {
            case "+":
                return leftVal + rightVal;
            case "-":
                return leftVal - rightVal;
            case "*":
                return leftVal * rightVal;
            default:
                if (Pattern.matches("^(0|[1-9][0-9]*)$", value)) return Integer.parseInt(value);
                else if (Pattern.matches("^([a-zA-Z_][a-zA-Z_0-9]*)$", value)) return varsTable.get(value);
                throw new UninitializedVariableException(String.format("Error: '%s' may have not been initialized", value));
        }
    }

    private static void printTree(ASTNode node) {
        if (node == null) return;
        printTree(node.getLeft());
        System.out.print(node.getValue() + " ");
        printTree(node.getRight());
    }
}