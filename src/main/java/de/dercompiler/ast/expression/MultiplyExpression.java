package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.token.OperatorToken;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.STAR;

public final class MultiplyExpression extends BinaryExpression {

    public MultiplyExpression(SourcePosition position, AbstractExpression lhs, AbstractExpression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof MultiplyExpression me) {
            return syntaxEqualLhsRhs(me);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return STAR;
    }
}
