package de.dercompiler.ast;

import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

/**
 * Represents a placeholder class member that could not be correctly parsed
 */
public final class ErrorClassMember extends ClassMember {

    /**
     * Creates a new ErrorClassMember
     * @param position The source code position
     */
    public ErrorClassMember(SourcePosition position) {
        super(position);
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitErrorClassMember(this);
    }

    @Override
    public boolean syntaxEquals(ASTNode node) {
        return false;
    }
}
