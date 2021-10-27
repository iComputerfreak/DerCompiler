package de.dercompiler.ast.expression;

public sealed abstract class PostfixExpression extends UnaryExpression permits ArrayAccess, FieldAccess {
    public PostfixExpression(AbstractExpression encapsulated) {
        super(encapsulated);
    }
}
