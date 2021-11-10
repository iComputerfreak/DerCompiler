package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

/**
 * Represents a custom type in MiniJava
 */
public final class CustomType extends BasicType {
    
    private final String identifier;

    /**
     * Creates a new CustomType
     * @param position The source code position
     * @param identifier The name of the type
     */
    public CustomType(SourcePosition position, String identifier) {
        super(position);
        this.identifier = identifier;
    }

    /**
     * Returns the name of the type
     */
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
