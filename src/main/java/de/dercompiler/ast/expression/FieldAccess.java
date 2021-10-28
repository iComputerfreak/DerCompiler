package de.dercompiler.ast.expression;

public final class FieldAccess extends PostfixExpression {

    String fieldName;
    public FieldAccess(AbstractExpression encapsulated, String fieldName) {
        super(encapsulated);
        this.fieldName = fieldName;
    }
}
