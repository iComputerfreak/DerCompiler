package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class BooleanValue extends PrimaryExpression {

    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof BooleanValue bv) {
            return value == bv.value;
        }
        return false;
    }
}
