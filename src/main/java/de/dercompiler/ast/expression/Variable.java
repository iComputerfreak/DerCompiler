package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class Variable extends PrimaryExpression {

    private String name;
    public Variable(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof Variable v) {
            return name.equals(v.name);
        }
        return false;
    }
}
