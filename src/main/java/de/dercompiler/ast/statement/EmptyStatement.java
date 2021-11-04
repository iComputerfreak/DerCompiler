package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class EmptyStatement extends Statement {

    public EmptyStatement(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof EmptyStatement) return true;
        return false;
    }
}
