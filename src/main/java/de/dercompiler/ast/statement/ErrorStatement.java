package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class ErrorStatement extends Statement{

    public ErrorStatement(SourcePosition position) {
        super(position);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        return  (other instanceof ErrorStatement);
    }

    @Override
    public void accept(ASTStatementVisitor asTStatementVisitor) {
        asTStatementVisitor.visitErrorStatement(this);
    }
}
