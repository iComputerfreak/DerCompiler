package de.dercompiler.ast.expression;

import de.dercompiler.ast.SyntaxEquatable;

import java.util.Objects;

public final class LogicalAndExpression extends BinaryExpression {

    public LogicalAndExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalAndExpression lae) {
            return syntaxEqualLhsRhs(lae);
        }
        return false;
    }
}
