package de.dercompiler.ast;

import de.dercompiler.lexer.SourcePosition;

public final class MethodRest implements ASTNode {

    private final SourcePosition position;
    private final String identifier;
    
    public MethodRest(SourcePosition position, String identifier) {
        this.position = position;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof MethodRest otherRest) {
            return this.identifier.equals(otherRest.identifier);
        }
        return false;
    }

    @Override
    public SourcePosition getSourcePosition() {
        return position;
    }
}
