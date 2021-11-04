package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class LessEqualExpression extends BinaryExpression {
    public LessEqualExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LessEqualExpression lee) {
            return syntaxEqualLhsRhs(lee);
        }
        return false;
    }
}
