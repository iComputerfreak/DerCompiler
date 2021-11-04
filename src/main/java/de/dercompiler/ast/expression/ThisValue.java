package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class ThisValue extends PrimaryExpression {

    public ThisValue(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof ThisValue) return true;
        return false;
    }
}
