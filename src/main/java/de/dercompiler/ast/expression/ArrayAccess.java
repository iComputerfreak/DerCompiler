package de.dercompiler.ast.expression;

public final class ArrayAccess extends PostfixExpression {

    private AbstractExpression arrayPosition;

    public ArrayAccess(AbstractExpression encapsulated, AbstractExpression arrayPosition) {
        super(encapsulated);
        this.arrayPosition = arrayPosition;
    }
}
