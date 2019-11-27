package com.brooklyn.edu.cisc3160.toylang.tokens;

public abstract class Token {
    protected Integer value;
    protected Type type;
    public static enum Type {
        ASSIGNMENT,
        EXPRESSION,
        IDENTIFIER,
        TERM,
        FACTOR
    };

    public abstract String toString();

    public void setValue(Integer value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
