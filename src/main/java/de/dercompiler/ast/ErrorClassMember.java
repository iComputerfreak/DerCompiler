package de.dercompiler.ast;

import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

public final class ErrorClassMember extends ClassMember {

    public ErrorClassMember(SourcePosition position) {
        super(position);
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitErrorClassMember(this);
    }
}
