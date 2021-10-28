package de.dercompiler.ast.expression;

public final class BooleanValue extends PrimaryExpression {

    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }
}
