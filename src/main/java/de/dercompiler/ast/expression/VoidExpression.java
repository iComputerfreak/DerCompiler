package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class VoidExpression extends AbstractExpression{

    public VoidExpression(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof VoidExpression) return true;
        return false;
    }
}
