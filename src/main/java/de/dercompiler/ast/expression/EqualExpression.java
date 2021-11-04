package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class EqualExpression extends BinaryExpression {

    public EqualExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof EqualExpression ee) {
            return syntaxEqualLhsRhs(ee);
        }
        return false;
    }
}
