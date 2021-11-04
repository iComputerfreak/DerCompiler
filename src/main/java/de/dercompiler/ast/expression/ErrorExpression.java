package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class ErrorExpression extends AbstractExpression {

    public ErrorExpression() {}

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ErrorExpression ee) {
            return true;
        }
        return false;
    }
}
