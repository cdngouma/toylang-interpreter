package com.brooklyn.edu.cisc3160.toylang.interpreter;

import com.brooklyn.edu.cisc3160.toylang.tokens.ExprNode;
import com.brooklyn.edu.cisc3160.toylang.tokens.SyntaxTree;

import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class Parser {
    private Stack<ExprNode> stack;
    private List<String> tokens;
    private int cursor;

    public SyntaxTree parse(List<String> tokens) {
        this.tokens = tokens;
        this.cursor = 0;
        try {
            return program();
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Error: ';' expected");
            return  null;
        }
    }

    // P -> A*
    private boolean program() throws IndexOutOfBoundsException {
        ParseTree parseTree = new ParseTree();

        while (this.cursor < this.tokens.size()) {
            if (assignment()) {
                // add A to tree
            } else {
                return false;
            }
        }
        return true;
    }

    // A -> I = E;
    private boolean assignment() throws IndexOutOfBoundsException {
        if (identifier()) {
            this.cursor++;
            if (this.tokens.get(cursor).compareTo("=") == 0) {
                this.cursor++;
                if (expression()) {
                    // check for closing semi-colon
                    if (this.tokens.get(cursor).compareTo(";") == 0) {
                        // add
                        this.cursor++;
                    } else {
                        System.err.println(String.format("Error: ';' expected"));
                        return false;
                    }
                } else {
                    System.err.println(String.format("Error: illegal start of expression"));
                    return false;
                }
            } else {
                System.err.println(String.format("Error: '=' expected"));
                return false;
            }
        } else {
            System.err.println(String.format("Error: illegal identifier"));
            return false;
        }
        return true;
    }

    private boolean identifier() throws IndexOutOfBoundsException {
        String IDENTIFIER = "^[a-zA-Z_][a-zA-Z_0-9]*$";
        return Pattern.matches(IDENTIFIER, this.tokens.get(cursor));
    }

    // E -> E + T | E - T | E
    // E -> TE'
    private boolean expression() throws IndexOutOfBoundsException {
        if (term()) {
            // add T to tree
            if (expressionPrime()) {
                // add E' to tree
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    // E' -> +TE' | -TE' | e
    private boolean expressionPrime() throws IndexOutOfBoundsException {
        if (Pattern.matches("^[+-]$", this.tokens.get(cursor))) {
            // add + ot - to tree
            this.cursor++;
            if (term()) {
                // add T to tree
                if (expressionPrime()) {
                    // add E' to tree
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    // T -> T * F | F
    // T -> FT'
    private boolean term() throws IndexOutOfBoundsException {
        if (factor()) {
            // add F to tree
            if (termPrime()) {
                // add T' to tree
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    // T' -> *FT' | e
    private boolean termPrime() throws IndexOutOfBoundsException {
        if (this.tokens.get(cursor).compareTo("*") == 0) {
            // add * to tree
            this.cursor++;
            if (factor()) {
                // add F to tree
                if (termPrime()) {
                    // add T' to tree
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    // F -> ( E ) | -F | +F | L | I
    private boolean factor() throws IndexOutOfBoundsException {
        if (this.tokens.get(cursor).compareTo("(") == 0) {
            // add { to tree
            this.cursor++;
            if (expression()) {
                // add E to tree
                if (this.tokens.get(cursor).compareTo(")") == 0) {
                    // add ) to tree
                    this.cursor++;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (Pattern.matches("^[+-]$", this.tokens.get(cursor))) {
            // add + or - to tree
            this.cursor++;
            if (factor()) {
                // add F to tree
            } else {
                return false;
            }
        } else if (Pattern.matches("^(0|[1-9][0-9]*)$", this.tokens.get(cursor))) {
            // add L to tree
            this.cursor++;
        } else if (identifier()) {
            // add I to tree
            this.cursor++;
        } else {
            return false;
        }
        return true;
    }
}
