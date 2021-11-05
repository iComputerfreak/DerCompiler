package de.dercompiler.ast;

import de.dercompiler.lexer.SourcePosition;

public abstract class ASTNode {
    
    private final SourcePosition sourcePosition;
    
    public ASTNode(SourcePosition sourcePosition) {
        this.sourcePosition = sourcePosition;
    }

    public boolean syntaxEquals(ASTNode other) {
        return other != null;
    }

    public SourcePosition getSourcePosition() {
        return this.sourcePosition;
    }

}
