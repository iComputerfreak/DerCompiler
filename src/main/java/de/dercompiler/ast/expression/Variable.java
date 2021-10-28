package de.dercompiler.ast.expression;

public final class Variable extends PrimaryExpression {

    private String name;
    public Variable(String name) {
        this.name = name;
    }
}
