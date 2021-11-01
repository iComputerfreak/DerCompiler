package de.dercompiler.ast.expression;

public abstract sealed class UnaryExpression extends AbstractExpression permits LogicalNotExpression, MethodInvocationOnObject, NegativeExpression, PostfixExpression {

    private AbstractExpression encapsulated;

    public UnaryExpression(AbstractExpression encapsulated) {
        this.encapsulated = encapsulated;
    }

    protected boolean syntaxEqualEncapsulated(UnaryExpression other) {
        return encapsulated.syntaxEqual(other.encapsulated);
    }
}
