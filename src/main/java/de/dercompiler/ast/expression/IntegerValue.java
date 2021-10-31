package de.dercompiler.ast.expression;

public final class IntegerValue extends PrimaryExpression {

    private String value;

    public IntegerValue(String value) {
        this.value = value;
    }
}
