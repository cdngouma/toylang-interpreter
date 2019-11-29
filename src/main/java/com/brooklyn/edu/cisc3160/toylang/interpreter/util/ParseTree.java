package com.brooklyn.edu.cisc3160.toylang.interpreter.util;

import java.util.ArrayList;
import java.util.List;

public class ParseTree {
    private List<ASTNode> children;

    public ParseTree() {
        this.children = new ArrayList<>();
    }

    public void add(ASTNode node) {
        this.children.add(node);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ASTNode a : this.children) {
            sb.append(a.toString()).append("\n");
        }
        return sb.toString();
    }

    public List<ASTNode> getChildren() {
        return this.children;
    }
}
