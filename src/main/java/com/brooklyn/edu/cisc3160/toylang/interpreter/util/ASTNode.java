package com.brooklyn.edu.cisc3160.toylang.interpreter.util;

/**
 * A Node of an Abstract Syntax Tree.
 */
public class ASTNode {
    private String value;
    private ASTNode left;
    private ASTNode right;

    public ASTNode(String value) {
        this.value = value;
    }

    public ASTNode(String value, ASTNode left, ASTNode right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public String getValue() {
        return value;
    }

    public ASTNode getLeft() {
        return left;
    }

    public ASTNode getRight() {
        return right;
    }
}
