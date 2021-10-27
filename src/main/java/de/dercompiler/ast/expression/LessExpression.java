package de.dercompiler.ast.expression;

public final class LessExpression extends BinaryExpression {
    public LessExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }
}
