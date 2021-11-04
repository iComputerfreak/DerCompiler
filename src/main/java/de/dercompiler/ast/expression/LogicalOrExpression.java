package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class LogicalOrExpression extends BinaryExpression {

    public LogicalOrExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalOrExpression loe) {
            return syntaxEqualLhsRhs(loe);
        }
        return false;
    }
}
