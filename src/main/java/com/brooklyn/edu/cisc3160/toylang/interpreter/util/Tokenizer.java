package com.brooklyn.edu.cisc3160.toylang.interpreter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Responsible for generating token from a source program.
 * @author Chrys Ngouma
 */
public class Tokenizer {
    /**
     *
     * @param input A string representation of the source program.
     * @return A list of token.
     */
    public static List<String> tokenize(String input) {
        final String RESTRICTED_SYMBOLS = "[+\\-*=\\\\/;,&><{}|()^%!\"'`:.?~]";
        final String OPERATORS = "[*/+-]";
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < input.length()) {
            StringBuilder token = new StringBuilder();
            String symbol = null;
            // sign is used for the case where a sequence of consecutive - or + precede a different token
            // e.g: -1 -> -1 | ----3 -> 3 | -+--+9 -> -9
            // * If the character - or + is preceded by an arithmetic operator, '(' or '=' then
            // compute the following consecutive sequence of + and/or - (including the current symbol)
            // until a different character is found (other than OPERATOR, '(' and '=') to have either -1 or 1.
            // Initialize sign to -1 if '-' if found first. Initialize to 1 otherwise. Multiply sign by -1 each time '-' is found.
            // add the result to the token list then add '*' to the list then add the next token to the list.
            // * Otherwise, add the symbol + or - to the tokens list then add the next token to the list.
            Integer sign = null;
            // While char is not a special character, keep looping until a white space is found to extract the full literal
            while (i < input.length()) {
                String s = String.valueOf(input.charAt(i));
                if (Pattern.matches("\\s", s)) {
                    break;
                } else if (s.compareTo("-") == 0 || s.compareTo("+") == 0) {
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
