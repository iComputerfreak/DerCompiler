package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class SubtractExpression extends BinaryExpression {

    public SubtractExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof SubtractExpression se) {
            return syntaxEqualLhsRhs(se);
        }
        return false;
    }
}
