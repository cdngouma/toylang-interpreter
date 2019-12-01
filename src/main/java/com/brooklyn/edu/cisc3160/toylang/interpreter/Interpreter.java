package com.brooklyn.edu.cisc3160.toylang.interpreter;

import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UnexpectedTokenException;
import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UninitializedVariableException;
import com.brooklyn.edu.cisc3160.toylang.interpreter.util.ASTNode;
import com.brooklyn.edu.cisc3160.toylang.interpreter.util.ParseTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Interpreter {
    /**
     * Generates a variable table from a given program source code.
     * @param program A string representation of the program source code.
     * @return The variable table generated from a given program source code.
     * @throws UnexpectedTokenException When the program does not follow the language CFS.
     * @throws UninitializedVariableException When an uninitialized variable is detected.
     */
    public Map<String, Integer> getVarsTable(String program) throws UnexpectedTokenException, UninitializedVariableException {
        Parser parser = new Parser();
        List<String> tokens = Tokenizer.tokenize(program);
        return buildVarsTable(parser.parse(tokens));
    }

    /**
     * Build a variable table from a Parse Tree.
     * @param parseTree The Parse Tree generated from the given program source code.
     * @return A variable table.
     * @throws UninitializedVariableException
     */
    private Map<String, Integer> buildVarsTable(ParseTree parseTree) throws UninitializedVariableException {
        Map<String, Integer> varsTable = new HashMap<>();
        for (ASTNode node : parseTree.getChildren()) {
            String var = node.getLeft().getValue();
            int value = evaluateAST(node.getRight(), varsTable);
            varsTable.put(var, value);
        }
        return varsTable;
    }

    /**
     * Evaluate each branch of the AST.
     * @param node An Abstract Syntax Tree Node.
     * @param varsTable A variable table.
     * @return The value obtained from evaluating a branch of the AST.
     * @throws UninitializedVariableException When an uninitialized variable is detected.
     */
    private int evaluateAST(ASTNode node, Map<String, Integer> varsTable) throws UninitializedVariableException {
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
                if (Pattern.matches("^(0|-?[1-9][0-9]*)$", value)) return Integer.parseInt(value);
                else if (Pattern.matches("^([a-zA-Z_][a-zA-Z_0-9]*)$", value)) return varsTable.get(value);
                throw new UninitializedVariableException(String.format("Error: '%s' may have not been initialized", value));
        }
    }
}
