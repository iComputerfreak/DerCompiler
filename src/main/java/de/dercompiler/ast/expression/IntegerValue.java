package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class IntegerValue extends PrimaryExpression {

    private String value;

    public IntegerValue(String value) {
        this.value = value;
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof IntegerValue iv) {
            return value.equals(iv.value);
        }
        return false;
    }
}
