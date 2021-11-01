package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class EqualExpression extends BinaryExpression {

    public EqualExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof EqualExpression ee) {
            return syntaxEqualLhsRhs(ee);
        }
        return false;
    }
}
