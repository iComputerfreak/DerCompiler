package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

public final class NotEqualExpression extends BinaryExpression {
    public NotEqualExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        return false;
    }
}
