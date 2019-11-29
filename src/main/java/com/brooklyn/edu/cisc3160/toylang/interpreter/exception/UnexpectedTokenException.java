package com.brooklyn.edu.cisc3160.toylang.interpreter.exception;

public class UnexpectedTokenException extends Exception {
    public UnexpectedTokenException(String message) {
        super(message);
    }
}
