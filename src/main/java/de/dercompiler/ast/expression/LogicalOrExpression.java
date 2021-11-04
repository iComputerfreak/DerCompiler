package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.OR_LAZY;

public final class LogicalOrExpression extends BinaryExpression {

    public LogicalOrExpression(SourcePosition position, AbstractExpression lhs, AbstractExpression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LogicalOrExpression loe) {
            return syntaxEqualLhsRhs(loe);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return OR_LAZY;
    }
}
