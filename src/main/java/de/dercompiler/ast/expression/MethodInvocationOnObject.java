package de.dercompiler.ast.expression;

public final class MethodInvocationOnObject extends UnaryExpression {

    private Arguments arguments;

    public MethodInvocationOnObject(AbstractExpression encapsulated, Arguments arguments) {
        super(encapsulated);
        this.arguments = arguments;
    }
}
