package com.brooklyn.edu.cisc3160.toylang.interpreter.exception;

public class UninitializedVariableException extends Exception {
    public UninitializedVariableException(String message) {
        super(message);
    }
}
