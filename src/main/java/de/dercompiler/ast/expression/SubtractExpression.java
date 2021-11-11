package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.MINUS;

public final class SubtractExpression extends BinaryExpression {

    public SubtractExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof SubtractExpression se) {
            return syntaxEqualLhsRhs(se);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return MINUS;
    }
}
