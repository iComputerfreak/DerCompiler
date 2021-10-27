package de.dercompiler.ast.expression;

public final class LogicalAndExpression extends BinaryExpression {

    public LogicalAndExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }
}
