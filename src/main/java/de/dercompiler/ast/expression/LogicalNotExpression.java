package de.dercompiler.ast.expression;

public final class LogicalNotExpression extends UnaryExpression {

    public LogicalNotExpression(AbstractExpression encapsulated) {
        super(encapsulated);
    }
}
