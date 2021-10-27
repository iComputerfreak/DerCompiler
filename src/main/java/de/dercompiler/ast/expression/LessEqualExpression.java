package de.dercompiler.ast.expression;

public final class LessEqualExpression extends BinaryExpression {
    public LessEqualExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }
}
