package com.brooklyn.edu.cisc3160.toylang.interpreter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Tokenizer {
    private final static String ALLOWED_SYMBOLS = "[+\\-*=\\\\/;,&><{}|()^%!\"'`:.?~]";

    public static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < input.length()) {
            StringBuilder token = new StringBuilder();
            String symbol = null;
            while (i < input.length()) {
                String s = String.valueOf(input.charAt(i));
                if (Pattern.matches("\\s", s)) {
                    break;
                } else if (Pattern.matches(ALLOWED_SYMBOLS, s)) {
                    symbol = s;
                    break;
                } else {
                    token.append(s);
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
