package de.dercompiler.ast.expression;

public final class ArrayAccess extends PostfixExpression {

    public ArrayAccess(AbstractExpression encapsulated) {
        super(encapsulated);
    }
}
