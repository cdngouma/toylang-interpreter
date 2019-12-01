package com.brooklyn.edu.cisc3160.toylang.interpreter;

import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UnexpectedTokenException;
import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UninitializedVariableException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class InterpreterTest {
    @Test
    public void testValidProgram() {
        String p0 = "x = 3;";
        String p1 = "x_2 = 10;";
        String p2 = "x = 3;\ny=5;\nz  =x - y*3;";
        String p3 = "x=1;\ny = 2;\nz = ---(x*y)*(x+-y);";

        Interpreter interpreter = new Interpreter();

        try {
            Map<String,Integer> expected0 = new HashMap<>();
            expected0.put("x", 3);

            Map<String,Integer> expected1 = new HashMap<>();
            expected1.put("x_2", 10);

            Map<String,Integer> expected2 = new HashMap<>();
            expected2.put("x", 3); expected2.put("y",5); expected2.put("z",-12);

            Map<String,Integer> expected3 = new HashMap<>();
            expected3.put("x", 1); expected3.put("y",2); expected3.put("z",2);

            Map<String,Integer> actual0 = interpreter.getVarsTable(p0);
            Map<String,Integer> actual1 = interpreter.getVarsTable(p1);
            Map<String,Integer> actual2 = interpreter.getVarsTable(p2);
            Map<String,Integer> actual3 = interpreter.getVarsTable(p3);

            assertThat("Basic program", actual0, is(expected0));
            assertThat("Underscored variable",actual1, is(expected1));
            assertThat("Multi-line program", actual2, is(expected2));
            assertThat("Sign multiplication", actual3, is(expected3));

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = UninitializedVariableException.class)
    public void testUninitializedVariable() throws UnexpectedTokenException, UninitializedVariableException {
        Interpreter interpreter = new Interpreter();
        String program = "x = 3;\nz  =x - y*3;";
        interpreter.getVarsTable(program);
    }

    @Test(expected = UnexpectedTokenException.class)
    public void testMissingSemicolon() throws UnexpectedTokenException, UninitializedVariableException {
        Interpreter interpreter = new Interpreter();
        String p0 = "x_2 = 10";
        interpreter.getVarsTable(p0);
    }
    @Test
    public void testEmptyProgram() throws UnexpectedTokenException, UninitializedVariableException {
        String p0 = "";
        Interpreter interpreter = new Interpreter();
        Map<String,Integer> expected0 = new HashMap<>();
        Map<String,Integer> actual0 = interpreter.getVarsTable(p0);
        assertThat("Empty program",actual0, is(expected0));
    }

    @Test(expected = UnexpectedTokenException.class)
    public void testInvalidLiteral() throws UnexpectedTokenException, UninitializedVariableException {
        Interpreter interpreter = new Interpreter();
        String p0 = "x = 001";
        interpreter.getVarsTable(p0);
    }

    @Test(expected = UnexpectedTokenException.class)
    public void testInvalidIdentifier() throws UnexpectedTokenException, UninitializedVariableException {
        Interpreter interpreter = new Interpreter();
        String p0 = "8 = x";
        interpreter.getVarsTable(p0);
    }

    @Test(expected = UnexpectedTokenException.class)
    public void testMissingAssignmentOperator() throws UnexpectedTokenException, UninitializedVariableException {
        Interpreter interpreter = new Interpreter();
        String p0 = "factorial5 5 * 4 * 3 * 2 * 1";
        interpreter.getVarsTable(p0);
    }

    @Test(expected = UnexpectedTokenException.class)
    public void testMissingArithmeticOperatorBetweenFactors() throws UnexpectedTokenException, UninitializedVariableException {
        Interpreter interpreter = new Interpreter();
        String p0 = "x = 2 3;";
        interpreter.getVarsTable(p0);
    }

    @Test(expected = UnexpectedTokenException.class)
    public void testMissingClosingParenthesis() throws UnexpectedTokenException, UninitializedVariableException {
        Interpreter interpreter = new Interpreter();
        String p0 = "x = (5 + 3;";
        interpreter.getVarsTable(p0);
    }
}