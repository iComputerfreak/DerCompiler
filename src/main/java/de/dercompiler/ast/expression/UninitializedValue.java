package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class UninitializedValue extends AbstractExpression {

    public UninitializedValue() { }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof UninitializedValue) return true;
        return false;
    }
}
