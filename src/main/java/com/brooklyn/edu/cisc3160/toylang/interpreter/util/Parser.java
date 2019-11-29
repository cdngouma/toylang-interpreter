package com.brooklyn.edu.cisc3160.toylang.interpreter.util;

import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UnexpectedTokenException;
import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UninitializedVariableException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

public class Parser {
    private int cursor;
    private List<String> tokens;
    private Set<String> initializedVars;
    private Stack<String> operatorStack;
    private Stack<ASTNode> exprStack;
    private StringBuilder programStr;

    private final String IDENTIFIER = "([a-zA-Z_][a-zA-Z_0-9]*)";
    private final String NUMBERS = "(0|[1-9][0-9]*)";
    private final String OPERATORS = "([*+-=])";

    public ParseTree parse(List<String> tokens) throws UnexpectedTokenException, UninitializedVariableException {
        this.initializedVars = new HashSet<>();
        this.operatorStack = new Stack<>();
        this.exprStack = new Stack<>();
        this.tokens = tokens;
        this.cursor = 0;
        this.programStr = new StringBuilder();

        try {
            ParseTree P = program();
            if (P != null) return P;
            throw new UnexpectedTokenException("Error: Could not parse tree");
        } catch (IndexOutOfBoundsException e) {
            throw new UnexpectedTokenException(String.format("Error: ';' expected:\n%s<< HERE >>", programStr.toString()));
        }
    }

    // P -> A*
    private ParseTree program() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        ParseTree parseTree = new ParseTree();
        while (this.cursor < this.tokens.size()) {
            ASTNode A = assignment();
            if (A != null) parseTree.add(A);
            else return null;
        }
        return parseTree;
    }

    // A -> I = E;
    private ASTNode assignment() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        String id = identifier();
        if (id != null) {
            updateStacks(id);
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;
            // match assignment operator '='
            if (this.tokens.get(cursor).compareTo("=") == 0) {
                updateStacks("=");
                programStr.append(this.tokens.get(cursor)).append(" ");
                this.cursor++;
                if (expression()) {
                    // check for closing semi-colon
                    if (this.tokens.get(cursor).compareTo(";") == 0) {
                        emptyStacks();
                        initializedVars.add(id);
                        ASTNode A = exprStack.pop();
                        System.err.println("expr: " + exprStack.size() + " op: " + operatorStack.size());
                        programStr.append(this.tokens.get(cursor)).append("\n");
                        this.cursor++;
                        return A;
                    } else {
                        programStr.append(this.tokens.get(cursor)).append(" ");
                        throw new UnexpectedTokenException(String.format("Error: ';' expected:\n%s<< HERE >>", programStr.toString()));
                    }
                } else {
                    return null;
                }
            } else {
                throw new UnexpectedTokenException(String.format("Error: '=' expected:\n%s<< HERE >>", programStr.toString()));
            }
        } else {
            throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
        }
    }

    private String identifier() throws IndexOutOfBoundsException {
        if (Pattern.matches(IDENTIFIER, this.tokens.get(cursor))) {
            return this.tokens.get(cursor);
        }
        return null;
    }

    // E -> E + T | E - T | E
    // E -> TE'
    private boolean expression() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        if (term()) {
            return expressionPrime();
        } else {
            throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
        }
    }

    // E' -> +TE' | -TE' | e
    private boolean expressionPrime() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        if (Pattern.matches("^[+-]$", this.tokens.get(cursor))) {
            // add + or - to tree
            String op = this.tokens.get(cursor);
            updateStacks(op);
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;

            if (term()) {
                return expressionPrime();
            } else {
                throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
            }
        }
        return true;
    }

    // T -> T * F | F
    // T -> FT'
    private boolean term() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        if (factor()) {
            return termPrime();
        } else {
            throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
        }
    }

    // T' -> *FT' | e
    private boolean termPrime() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        if (this.tokens.get(cursor).compareTo("*") == 0) {
            // add * to tree
            String op = this.tokens.get(cursor);
            updateStacks(op);
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;
            if (factor()) {
                return termPrime();
            } else {
                throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
            }
        }
        return true;
    }

    // F -> ( E ) | -F | +F | L | I
    private boolean factor() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        if (this.tokens.get(cursor).compareTo("(") == 0) {
            updateStacks("(");
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;
            // check if '(' is followed by valid Expression
            if (expression()) {
                // look for closing ')'
                if (this.tokens.get(cursor).compareTo(")") == 0) {
                    updateStacks(")");
                    programStr.append(this.tokens.get(cursor)).append(" ");
                    this.cursor++;
                } else {
                    throw new UnexpectedTokenException(String.format("Error: ')' expected:\n%s<< HERE >>", programStr.toString()));
                }
            } else {
                throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
            }
        } else if (Pattern.matches("^[+-]$", this.tokens.get(cursor))) {
            // add + or - to tree
            String op = this.tokens.get(cursor);
            updateStacks(op);
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;
            return factor();
        } else if (Pattern.matches("^" + NUMBERS + "$", this.tokens.get(cursor))) {
            String d = this.tokens.get(cursor);
            updateStacks(d);
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;
        } else {
            String id = identifier();
            if (id != null && initializedVars.contains(id)) {
                updateStacks(id);
                programStr.append(this.tokens.get(cursor)).append(" ");
                this.cursor++;
                return true;
            } else if (id != null){
                throw new UninitializedVariableException(String.format("Error: '%s' may have not been initialized:\n%s<< HERE >>", id, programStr.toString()));
            } else {
                throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
            }
        }
        return true;
    }

    private void emptyStacks() {
        while (!operatorStack.isEmpty()) {
            String operator = operatorStack.pop();
            // The second operand was pushed last.
            ASTNode n2 = exprStack.isEmpty() ? null : exprStack.pop();
            ASTNode n1 = exprStack.isEmpty() ? null : exprStack.pop();
            exprStack.push(new ASTNode(operator, n1, n2));
        }
        // Pop the '(' off the operator stack.
        //operatorStack.pop();
    }

    private void updateStacks(String o) {
        if (Pattern.matches("^" + NUMBERS + "|" + IDENTIFIER + "$", o)) {
            exprStack.add(new ASTNode(o));
        }
        else if (o.compareTo("(") == 0) {
            operatorStack.push(o);
        }
        else if (Pattern.matches(OPERATORS, o)) {
            while (!operatorStack.isEmpty() && opPrecedence(operatorStack.peek()) >= opPrecedence(o)) {
                String operator = operatorStack.pop();
                // The second operand was pushed last
                ASTNode n2 = exprStack.isEmpty() ? null : exprStack.pop();
                ASTNode n1 = exprStack.isEmpty() ? null : exprStack.pop();
                exprStack.push(new ASTNode(operator, n1, n2));
            }
            // push operator onto stack
            operatorStack.push(o);
        }
        else if (o.compareTo(")") == 0) {
            while (!operatorStack.isEmpty() && operatorStack.peek().compareTo("(") != 0) {
                String operator = operatorStack.pop();
                // The second operand was pushed last.
                ASTNode n2 = exprStack.isEmpty() ? null : exprStack.pop();
                ASTNode n1 = exprStack.isEmpty() ? null : exprStack.pop();
                exprStack.push(new ASTNode(operator, n1, n2));
            }
            // Pop the '(' off the operator stack.
            operatorStack.pop();
        }
    }

    private int opPrecedence(String op) {
        switch (op) {
            case "=":
                return 0;
            case "+": case "-":
                return 1;
            case "*":
                return 2;
            case "(": case ")":
                return 3;
            default:
                throw new IllegalArgumentException(String.format("Operator unknown: %s:\n%s<< HERE >>", op, programStr.toString()));
        }
    }
}
