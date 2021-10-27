package de.dercompiler.ast.expression;

public final class LogicalOrExpression extends BinaryExpression {

    public LogicalOrExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }
}
