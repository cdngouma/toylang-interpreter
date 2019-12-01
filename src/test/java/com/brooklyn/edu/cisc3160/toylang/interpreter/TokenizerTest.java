package com.brooklyn.edu.cisc3160.toylang.interpreter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TokenizerTest {
    @Test
    public void testEmptyProgram() {
        String program = "";
        List<String> actual = Tokenizer.tokenize(program);
        List<String> expected = new ArrayList<>();
        assertThat(actual, is(expected));
    }

    @Test
    public void testNegativeNumber() {
        String p1 = "x = -2;";
        String p2 = "x = ----x;";
        String p3 = "x = 2 - 9;";

        List<String> actual1 = Tokenizer.tokenize(p1);
        List<String> actual2 = Tokenizer.tokenize(p2);
        List<String> actual3 = Tokenizer.tokenize(p3);

        List<String> expected1 = Arrays.asList("x","=","-1","*","2",";");
        List<String> expected2 = Arrays.asList("x","=","1","*","x",";");
        List<String> expected3 = Arrays.asList("x","=","2","-","9",";");

        assertThat("Negative number", actual1, is(expected1));
        assertThat("Sign multiplication", actual2, is(expected2));
        assertThat("Subtraction", actual3, is(expected3));
    }
}