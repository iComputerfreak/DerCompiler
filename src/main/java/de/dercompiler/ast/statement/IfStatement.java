package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class IfStatement extends Statement {

    AbstractExpression condition;
    Statement thenStatement;
    Statement elseStatement;

    public IfStatement(SourcePosition position, AbstractExpression condition, Statement thenStatement, Statement elseStatement) {
        super(position);
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    public boolean hasElse() {
        return Objects.nonNull(elseStatement);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof IfStatement is) {
            boolean result = condition.syntaxEquals(is.condition) && thenStatement.syntaxEquals(is.thenStatement);
            if (hasElse() && is.hasElse()) {
                return result && elseStatement.syntaxEquals(is.elseStatement);
                //xor hasElse() and is.hasElse() results in true, if one is null and the other is not, so ifs are not equal
            } else if (hasElse() ^ is.hasElse()) {
                return false;
            } else {
                return result;
            }
        }
        return false;
    }
}
