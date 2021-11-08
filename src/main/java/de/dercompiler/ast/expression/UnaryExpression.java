package de.dercompiler.ast.expression;

import de.dercompiler.lexer.SourcePosition;

public abstract sealed class UnaryExpression extends Expression permits LogicalNotExpression, MethodInvocationOnObject, NegativeExpression, PostfixExpression {

    protected Expression encapsulated;

    public UnaryExpression(SourcePosition position, Expression encapsulated) {
        super(position);
        this.encapsulated = encapsulated;
    }

    protected boolean syntaxEqualEncapsulated(UnaryExpression other) {
        return encapsulated.syntaxEquals(other.encapsulated);
    }

    public Expression getEncapsulated() {
        return encapsulated;
    }
}
