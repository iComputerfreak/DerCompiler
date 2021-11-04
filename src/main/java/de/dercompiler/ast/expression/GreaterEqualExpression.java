package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class GreaterEqualExpression extends BinaryExpression {
    public GreaterEqualExpression(SourcePosition position, AbstractExpression lhs, AbstractExpression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof GreaterEqualExpression gee) {
            return syntaxEqualLhsRhs(gee);
        }
        return false;
    }
}
