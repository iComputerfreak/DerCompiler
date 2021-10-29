package de.dercompiler.ast.expression;

public sealed class UnaryExpression extends AbstractExpression permits LogicalNotExpression, MethodInvocationOnObject, NegativeExpression, PostfixExpression {

    private AbstractExpression encapsulated;

    public UnaryExpression(AbstractExpression encapsulated) {
        this.encapsulated = encapsulated;
    }
}
