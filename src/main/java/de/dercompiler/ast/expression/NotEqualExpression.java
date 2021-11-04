package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class NotEqualExpression extends BinaryExpression {
    public NotEqualExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof NotEqualExpression nee) {
            return syntaxEqualLhsRhs(nee);
        }
        return false;
    }
}
