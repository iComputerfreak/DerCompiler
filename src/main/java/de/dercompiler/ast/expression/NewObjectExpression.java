package de.dercompiler.ast.expression;

public final class NewObjectExpression extends UnaryExpression {

    public NewObjectExpression(AbstractExpression encapsulated) {
        super(encapsulated);
    }
}
