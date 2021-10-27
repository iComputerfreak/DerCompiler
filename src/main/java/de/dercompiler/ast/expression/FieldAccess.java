package de.dercompiler.ast.expression;

public final class FieldAccess extends PostfixExpression {

    public FieldAccess(AbstractExpression encapsulated) {
        super(encapsulated);
    }
}
