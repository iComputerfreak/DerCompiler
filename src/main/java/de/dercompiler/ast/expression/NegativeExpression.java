package de.dercompiler.ast.expression;

public final class NegativeExpression extends UnaryExpression {

    public NegativeExpression(AbstractExpression encapsulated) {
        super(encapsulated);
    }
}
