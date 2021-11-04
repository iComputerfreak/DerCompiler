package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class LogicalAndExpression extends BinaryExpression {

    public LogicalAndExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalAndExpression lae) {
            return syntaxEqualLhsRhs(lae);
        }
        return false;
    }
}
