package de.dercompiler.ast.expression;

import de.dercompiler.lexer.SourcePosition;

public abstract sealed class UnaryExpression extends AbstractExpression permits LogicalNotExpression, MethodInvocationOnObject, NegativeExpression, PostfixExpression {

    private AbstractExpression encapsulated;

    public UnaryExpression(SourcePosition position, AbstractExpression encapsulated) {
        super(position);
        this.encapsulated = encapsulated;
    }

    protected boolean syntaxEqualEncapsulated(UnaryExpression other) {
        return encapsulated.syntaxEquals(other.encapsulated);
    }
}
