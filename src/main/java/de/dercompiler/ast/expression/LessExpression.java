package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.LESS_THAN;


public final class LessExpression extends BinaryExpression {
    public LessExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public OperatorToken getOperator() {
        return LESS_THAN;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LessExpression le) {
            return syntaxEqualLhsRhs(le);
        }
        return false;
    }
}
