package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class GreaterExpression extends BinaryExpression {
    public GreaterExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof GreaterExpression ge) {
            return syntaxEqualLhsRhs(ge);
        }
        return false;
    }
}
