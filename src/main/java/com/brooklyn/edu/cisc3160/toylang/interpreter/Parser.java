package com.brooklyn.edu.cisc3160.toylang.interpreter;

import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UnexpectedTokenException;
import com.brooklyn.edu.cisc3160.toylang.interpreter.exception.UninitializedVariableException;
import com.brooklyn.edu.cisc3160.toylang.interpreter.util.ASTNode;
import com.brooklyn.edu.cisc3160.toylang.interpreter.util.ParseTree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Responsible of producing a Parse Tree for a given CFS.
 */
public class Parser {
    private int cursor;
    private List<String> tokens;
    private Set<String> initializedVars;
    // Needed to construct the AST using Shunting-yard algorithm
    private Stack<String> operatorStack;
    private Stack<ASTNode> exprStack;
    // Used to show exactly when a error is detected on the prgram.
    private StringBuilder programStr;

    private final String IDENTIFIER = "([a-zA-Z_][a-zA-Z_0-9]*)";
    private final String NUMBERS = "(0|-?[1-9][0-9]*)";
    private final String OPERATORS = "([*+-=])";

    /**
     * Produce a Parse Tree given a list of tokens.
     * @param tokens A list of tokens.
     * @return A Parse Tree.
     * @throws UnexpectedTokenException When the sequence of tokens does not match the CFS.
     * @throws UninitializedVariableException When an uninitialized variable is detected.
     */
    public ParseTree parse(List<String> tokens) throws UnexpectedTokenException, UninitializedVariableException {
        this.initializedVars = new HashSet<>();
        this.operatorStack = new Stack<>();
        this.exprStack = new Stack<>();
        this.tokens = tokens;
        this.cursor = 0;
        this.programStr = new StringBuilder();

        try {
            ParseTree P = program();
            if (P != null) return P;
            throw new UnexpectedTokenException("Error: Could not parse tree");
        } catch (IndexOutOfBoundsException e) {
            throw new UnexpectedTokenException(String.format("Error: ';' expected:\n%s<< HERE >>", programStr.toString()));
        }
    }

    // P -> A*
    private ParseTree program() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        ParseTree parseTree = new ParseTree();
        while (this.cursor < this.tokens.size()) {
            ASTNode A = assignment();
            if (A != null) parseTree.add(A);
            else return null;
        }
        return parseTree;
    }

    /**
     * Verify if the sequence of token matches the CFS: Assignment :: Identifier = Expression
     * @return A Syntax Tree Node.
     */
    private ASTNode assignment() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        // A -> I = E
        String id = identifier();
        if (id != null) {
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;
            // check for assignment operator '='
            if (this.tokens.get(cursor).compareTo("=") == 0) {
                programStr.append(this.tokens.get(cursor)).append(" ");
                this.cursor++;
                if (expression()) {
                    // check for closing semi-colon
                    if (this.tokens.get(cursor).compareTo(";") == 0) {
                        emptyStacks();
                        initializedVars.add(id);
                        ASTNode E = exprStack.pop();
                        ASTNode I = new ASTNode(id);
                        ASTNode A = new ASTNode("=",I,E);
                        programStr.append(this.tokens.get(cursor)).append("\n");
                        this.cursor++;
                        return A;
                    } else {
                        programStr.append(this.tokens.get(cursor)).append(" ");
                        throw new UnexpectedTokenException(String.format("Error: ';' expected:\n%s<< HERE >>", programStr.toString()));
                    }
                } else {
                    return null;
                }
            } else {
                throw new UnexpectedTokenException(String.format("Error: '=' expected:\n%s<< HERE >>", programStr.toString()));
            }
        } else {
            throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
        }
    }

    /**
     * Verify if the given token is an Identifier.
     * @return A string representation of a variable name or null (if not an Identifier).
     */
    private String identifier() throws IndexOutOfBoundsException {
        if (Pattern.matches(IDENTIFIER, this.tokens.get(cursor))) {
            return this.tokens.get(cursor);
        }
        return null;
    }

    /**
     * Verify if the sequence of token matches the CFS: Expression :: TermExpression_Prime.
     * @return True if the CFS is matched at the current level; False otherwise.
     */
    private boolean expression() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        // E -> TE'
        if (term()) {
            return expressionPrime();
        } else {
            throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
        }
    }

    /**
     * Verify if the sequence of token matches the CFS: ExpressionPrime :: +TermExpression_Prime | -TermExpression_Prime | null
     * @return True if the CFS is matched at the current level; False otherwise.
     */
    private boolean expressionPrime() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        // E' -> +TE' | -TE' | e
        if (Pattern.matches("^[+-]$", this.tokens.get(cursor))) {
            // add + or - to tree
            String op = this.tokens.get(cursor);
            updateStacks(op);
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;

            if (term()) {
                return expressionPrime();
            } else {
                throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
            }
        }
        return true;
    }

    /**
     * Verify if the sequence of token matches the CFS: Term :: FactorTerm_Prime
     * @return True if the CFS is matched at the current level; False otherwise.
     */
    private boolean term() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        // T -> FT'
        if (factor()) {
            return termPrime();
        } else {
            throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
        }
    }

    /**
     * Verify if the sequence of token matches the CFS: Term_Prime :: *FactorTerm_Prime
     * @return True if the CFS is matched at the current level; False otherwise.
     * @throws UnexpectedTokenException When the CFS is not matched at the current level.
     */
    private boolean termPrime() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        // T' -> *FT'
        if (this.tokens.get(cursor).compareTo("*") == 0) {
            // add * to tree
            String op = this.tokens.get(cursor);
            updateStacks(op);
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;
            if (factor()) {
                return termPrime();
            } else {
                throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
            }
        }
        return true;
    }

    // F -> ( E ) | -F | +F | L | I

    /**
     * Verify if the sequence of token matches the CFS: Factor :: (Expression) | -Factor | +Factor | Literal | Identifier
     * @return True if the CFS is matched at the current level; False otherwise.
     * @throws UninitializedVariableException When an uninitialized variable is detected.
     */
    private boolean factor() throws IndexOutOfBoundsException, UnexpectedTokenException, UninitializedVariableException {
        if (this.tokens.get(cursor).compareTo("(") == 0) {
            updateStacks("(");
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;
            // check if '(' is followed by valid Expression
            if (expression()) {
                // look for closing ')'
                if (this.tokens.get(cursor).compareTo(")") == 0) {
                    updateStacks(")");
                    programStr.append(this.tokens.get(cursor)).append(" ");
                    this.cursor++;
                } else {
                    throw new UnexpectedTokenException(String.format("Error: ')' expected:\n%s<< HERE >>", programStr.toString()));
                }
            } else {
                throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
            }
        } else if (Pattern.matches("^[+-]$", this.tokens.get(cursor))) {
            // add + or - to tree
            String op = this.tokens.get(cursor);
            updateStacks(op);
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;
            return factor();
        } else if (Pattern.matches("^" + NUMBERS + "$", this.tokens.get(cursor))) {
            String d = this.tokens.get(cursor);
            updateStacks(d);
            programStr.append(this.tokens.get(cursor)).append(" ");
            this.cursor++;
        } else {
            String id = identifier();
            if (id != null && initializedVars.contains(id)) {
                updateStacks(id);
                programStr.append(this.tokens.get(cursor)).append(" ");
                this.cursor++;
                return true;
            } else if (id != null){
                throw new UninitializedVariableException(String.format("Error: '%s' may have not been initialized:\n%s<< HERE >>", id, programStr.toString()));
            } else {
                throw new UnexpectedTokenException(String.format("Error: Illegal start of expression:\n%s<< HERE >>", programStr.toString()));
            }
        }
        return true;
    }

    private void emptyStacks() {
        while (!operatorStack.isEmpty()) {
            String operator = operatorStack.pop();
            // The second operand was pushed last.
            ASTNode n2 = exprStack.isEmpty() ? null : exprStack.pop();
            ASTNode n1 = exprStack.isEmpty() ? null : exprStack.pop();
            exprStack.push(new ASTNode(operator, n1, n2));
        }
    }

    // Uses Shunting-yard algorithm to construct a Abstract Syntax Tree
    // https://en.wikipedia.org/wiki/Shunting-yard_algorithm
    private void updateStacks(String token) {
        if (Pattern.matches("^" + NUMBERS + "|" + IDENTIFIER + "$", token)) {
            exprStack.add(new ASTNode(token));
        }
        else if (token.compareTo("(") == 0) {
            operatorStack.push(token);
        }
        else if (Pattern.matches(OPERATORS, token)) {
            while (!operatorStack.isEmpty() && opPrecedence(operatorStack.peek()) >= opPrecedence(token)) {
                String operator = operatorStack.pop();
                // The second operand was pushed last
                ASTNode n2 = exprStack.isEmpty() ? null : exprStack.pop();
                ASTNode n1 = exprStack.isEmpty() ? null : exprStack.pop();
                exprStack.push(new ASTNode(operator, n1, n2));
            }
            // push operator onto stack
            operatorStack.push(token);
        }
        else if (token.compareTo(")") == 0) {
            while (!operatorStack.isEmpty() && operatorStack.peek().compareTo("(") != 0) {
                String operator = operatorStack.pop();
                // The second operand was pushed last.
                ASTNode n2 = exprStack.isEmpty() ? null : exprStack.pop();
                ASTNode n1 = exprStack.isEmpty() ? null : exprStack.pop();
                exprStack.push(new ASTNode(operator, n1, n2));
            }
            // Pop the '(' off the operator stack.
            if (!operatorStack.isEmpty()) operatorStack.pop();
        }
    }

    /**
     * Determine an operator precedence
     * @param op An operator.
     * @return The operator precedence value.
     */
    private int opPrecedence(String op) throws IllegalArgumentException {
        switch (op) {
            case "(": case ")":
                return 0;
            case "+": case "-":
                return 1;
            case "*":
                return 2;
            default:
                throw new IllegalArgumentException(String.format("Operator unknown: %s", op));
        }
    }
}
