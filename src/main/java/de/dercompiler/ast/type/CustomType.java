package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

public final class CustomType extends BasicType {
    
    private final String identifier;

    public CustomType(SourcePosition position, String identifier) {
        super(position);
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

    @Override
    public String toString() {
        return getIdentifier();
    }
}
