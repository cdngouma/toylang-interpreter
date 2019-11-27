package com.brooklyn.edu.cisc3160.toylang.tokens;

public class ExprNode extends Token {
    private String operator;
    private Token leftOperand;
    private Token rightOperand;

    public ExprNode(String operator, Token leftOperand, Token rightOperand) throws Exception {
        this.operator = operator;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.setType();
        this.setValue();
    }

    private void setType() throws Exception {
        switch (this.operator) {
            case "=":
                this.type = Type.ASSIGNMENT;
                break;
            case "+": case "-":
                this.type = Type.EXPRESSION;
                break;
            case "*":
                this.type = Type.TERM;
                break;
            default:
                throw new Exception("Unexpected expression");
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", this.leftOperand.toString(), this.operator, this.rightOperand.toString());
    }

    private void setValue() throws Exception {
        switch (operator) {
            case "=":
                if (this.leftOperand.type != Token.Type.IDENTIFIER) {
                    throw new Exception("Trying to assign value to non-identifier token");
                }
                this.leftOperand.setValue(this.rightOperand.getValue());
                break;
            case "+":
                this.value = this.leftOperand.getValue() + this.rightOperand.getValue();
                break;
            case "-":
                this.value = this.leftOperand.getValue() - this.rightOperand.getValue();
                break;
            case "*":
                this.value = this.leftOperand.getValue() * this.rightOperand.getValue();
                break;
            default:
                throw new Exception(String.format("Unsupported operator \"%s\"", this.operator));
        }
    }
}
