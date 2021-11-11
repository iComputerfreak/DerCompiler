package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.AND_LAZY;

public final class LogicalAndExpression extends BinaryExpression {

    public LogicalAndExpression(SourcePosition position, Expression lhs, Expression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalAndExpression lae) {
            return syntaxEqualLhsRhs(lae);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return AND_LAZY;
    }
}
