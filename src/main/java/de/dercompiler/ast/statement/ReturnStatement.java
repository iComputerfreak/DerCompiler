package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.AbstractExpression;

import java.util.Objects;

public final class ReturnStatement extends Statement {

    AbstractExpression returnExpression;

    public ReturnStatement(AbstractExpression returnExpression) {
        this.returnExpression = returnExpression;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ReturnStatement rs) {
            return returnExpression.syntaxEqual(rs.returnExpression);
        }
        return false;
    }
}
