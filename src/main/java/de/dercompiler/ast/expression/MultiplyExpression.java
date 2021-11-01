package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class MultiplyExpression extends BinaryExpression {

    public MultiplyExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof MultiplyExpression me) {
            return syntaxEqualLhsRhs(me);
        }
        return false;
    }
}
