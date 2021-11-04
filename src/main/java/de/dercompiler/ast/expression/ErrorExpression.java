package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class ErrorExpression extends AbstractExpression {

    public ErrorExpression(SourcePosition position) { super(position); }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ErrorExpression ee) {
            return true;
        }
        return false;
    }
}
