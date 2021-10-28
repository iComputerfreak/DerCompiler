package de.dercompiler.ast.expression;

public final class MethodeInvocationOnObject extends UnaryExpression {

    private Arguments arguments;

    public MethodeInvocationOnObject(AbstractExpression encapsulated, Arguments arguments) {
        super(encapsulated);
        this.arguments = arguments;
    }
}
