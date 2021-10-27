package de.dercompiler.ast.expression;

public final class EqualExpression extends BinaryExpression {

    public EqualExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }
}
