package de.dercompiler.ast;

public final class MethodRest implements ASTNode {
    
    private final String identifier;
    
    public MethodRest(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (other instanceof MethodRest otherRest) {
            return this.identifier.equals(otherRest.identifier);
        }
        return false;
    }
}
