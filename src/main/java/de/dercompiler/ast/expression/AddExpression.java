package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class AddExpression extends BinaryExpression {

    public AddExpression(AbstractExpression lhs, AbstractExpression rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (this == other) return true;
        if (other instanceof AddExpression ae) {
            return syntaxEqualLhsRhs(ae);
        }
        return false;
    }
}
