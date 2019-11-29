package com.brooklyn.edu.cisc3160.toylang.interpreter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Tokenizer {

    public static List<String> tokenize(String input) {
        final String RESTRICTED_SYMBOLS = "[+\\-*=\\\\/;,&><{}|()^%!\"'`:.?~]";
        final String OPERATORS = "[*/+-]";
        List<String> tokens = new ArrayList<>();
        int i = 0;
        // flag and sign are used for the case where a sequence of consecutive - or + precede a different token
        // e.g: -1 -> -1 | ----3 -> 3 | -+--+9 -> -9
        // set flag to True when the symbols + or - are found
        // compute the following consecutive sequence of + and/or - to have either -1 or 1
        // multiply the result by the next token.
        //boolean flag = false;
        //Integer sign = null;

        while (i < input.length()) {
            StringBuilder token = new StringBuilder();
            String symbol = null;
            Integer sign = null;
            // While char is not a symbol, keep looping until a white space is found to extract the full literal
            while (i < input.length()) {
                String s = String.valueOf(input.charAt(i));
                if (Pattern.matches("\\s", s)) {
                    break;
                } else if (s.compareTo("-") == 0 || s.compareTo("+") == 0) {
                    //if (sign == null && (token.length() > 0 || (tokens.size() > 0 && !Pattern.matches(OPERATORS,tokens.get(tokens.size() - 1))))) {
                    if (sign == null) {
                        int k = tokens.size();
                        if (token.length() > 0 || (k > 0 && !Pattern.matches(OPERATORS + "|[(=]", tokens.get(k - 1)))) {
                            symbol = s;
                            break;
                        } else if (s.compareTo("-") == 0){
                            sign = -1;
                        } else {
                            sign = 1;
                        }
                    } else if (s.compareTo("-") == 0){
                        sign *= -1;
                    }
                } else {
                    if (sign != null) {
                        tokens.add(String.valueOf(sign));
                        tokens.add("*");
                        sign = null;
                    }

                    if (Pattern.matches(RESTRICTED_SYMBOLS, s)) {
                        symbol = s;
                        break;
                    } else {
                        token.append(s);
                    }
                }
                i++;
            }

            if (token.length() > 0) {
                tokens.add(token.toString());
            }
            if (symbol != null) {
                tokens.add(symbol);
            }
            i++;
        }

        return tokens;
    }
}
