package com.brooklyn.edu.cisc3160.toylang.tokens;

public class IdentifierNode extends Token {
    private String name;

    public IdentifierNode(String name) {
        this.type = Type.IDENTIFIER;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", this.name, this.value);
    }

    public String getName() {
        return this.name;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
