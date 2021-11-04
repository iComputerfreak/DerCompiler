package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class DivisionExpression extends BinaryExpression {

    public DivisionExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof DivisionExpression de) {
            return syntaxEqualLhsRhs(de);
        }
        return false;
    }
}
