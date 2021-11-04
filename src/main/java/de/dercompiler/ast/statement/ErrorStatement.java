package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.expression.ErrorExpression;

import java.util.Objects;

public final class ErrorStatement extends Statement{
    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        return  (other instanceof ErrorStatement);
    }
}
