package com.brooklyn.edu.cisc3160.toylang.interpreter.util;

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

    public void setValue(String value) {
        this.value = value;
    }

    public ASTNode getLeft() {
        return left;
    }

    public void setLeft(ASTNode left) {
        this.left = left;
    }

    public ASTNode getRight() {
        return right;
    }

    public void setRight(ASTNode right) {
        this.right = right;
    }
}
