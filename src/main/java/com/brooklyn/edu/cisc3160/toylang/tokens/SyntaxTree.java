package com.brooklyn.edu.cisc3160.toylang.tokens;

import java.util.LinkedList;
import java.util.List;

public class SyntaxTree {
    private List<ExprNode> assignmentNodes;

    public SyntaxTree() {
        this.assignmentNodes = new LinkedList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ExprNode a : this.assignmentNodes) {
            sb.append(a.toString()).append("\n");
        }
        return sb.toString();
    }

    public void addAssignment(ExprNode assignment) throws Exception {
        if (assignment.type == Token.Type.ASSIGNMENT) {
            this.assignmentNodes.add(assignment);
        } else {
            throw new Exception("Not a assignment expression");
        }
    }
}
