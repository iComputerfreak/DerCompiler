package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class ErrorExpression extends AbstractExpression {

    public ErrorExpression() {}

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ErrorExpression ee) {
            return true;
        }
        return false;
    }
}
