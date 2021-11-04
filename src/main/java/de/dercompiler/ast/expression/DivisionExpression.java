package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.token.OperatorToken;

import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.SLASH;

public final class DivisionExpression extends BinaryExpression {

    public DivisionExpression(SourcePosition position, AbstractExpression lhs, AbstractExpression rhs) {
        super(position, lhs, rhs);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof DivisionExpression de) {
            return syntaxEqualLhsRhs(de);
        }
        return false;
    }

    @Override
    public OperatorToken getOperator() {
        return SLASH;
    }
}
