package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class ThisValue extends PrimaryExpression {

    public ThisValue() { }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ThisValue) return true;
        return false;
    }
}
