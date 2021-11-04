package de.dercompiler.ast.type;

import de.dercompiler.ast.SyntaxEquatable;

public final class CustomType extends BasicType {
    
    private final String identifier;

    public CustomType(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (other instanceof CustomType otherType) {
            return this.identifier.equals(otherType.identifier);
        }
        return false;
    }
}
