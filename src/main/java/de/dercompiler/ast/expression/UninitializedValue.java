package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class UninitializedValue extends AbstractExpression {

    public UninitializedValue() { }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof UninitializedValue) return true;
        return false;
    }
}
