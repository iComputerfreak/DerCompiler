package de.dercompiler.ast;

import de.dercompiler.ast.type.Type;

public final class Parameter implements ASTNode {
    
    private final Type type;
    private final String identifier;
    
    public Parameter(Type type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof Parameter otherParam) {
            return this.type.syntaxEquals(otherParam.type)
                    && this.identifier.equals(otherParam.identifier);
        }
        return false;
    }
}
