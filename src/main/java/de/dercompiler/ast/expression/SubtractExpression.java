package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class SubtractExpression extends BinaryExpression {

    public SubtractExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof SubtractExpression se) {
            return syntaxEqualLhsRhs(se);
        }
        return false;
    }
}
