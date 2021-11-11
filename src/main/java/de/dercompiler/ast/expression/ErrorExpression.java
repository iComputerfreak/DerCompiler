package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class ErrorExpression extends Expression {

    public ErrorExpression(SourcePosition position) { super(position); }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        return other instanceof ErrorExpression ee;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitErrorExpression(this);
    }
}
