package de.dercompiler.ast.expression;

public final class MethodInvocationOnObject extends UnaryExpression {

    private Arguments arguments;
    private String functionName;

    public MethodInvocationOnObject(AbstractExpression encapsulated, String functionName, Arguments arguments) {
        super(encapsulated);
        this.functionName = functionName;
        this.arguments = arguments;
    }
}
