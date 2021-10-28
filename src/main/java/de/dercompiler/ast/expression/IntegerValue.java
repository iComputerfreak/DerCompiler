package de.dercompiler.ast.expression;

public final class IntegerValue extends PrimaryExpression {

    private int value;

    public IntegerValue(int value) {
        this.value = value;
    }
}
