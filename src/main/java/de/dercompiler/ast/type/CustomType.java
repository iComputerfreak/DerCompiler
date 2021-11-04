package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class CustomType extends BasicType {
    
    private final String identifier;

    public CustomType(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof CustomType otherType) {
            return this.identifier.equals(otherType.identifier);
        }
        return false;
    }
}
