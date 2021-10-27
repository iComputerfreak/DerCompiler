package de.dercompiler.ast.expression;

public final class NotEqualExpression extends BinaryExpression {
    public NotEqualExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }
}
