package de.dercompiler.ast;

import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

public final class Field extends ClassMember {

    private final Type type;
    private final String identifier;

    public Field(SourcePosition position, Type type, String identifier) {
        super(position);
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
        if (other instanceof Field otherField) {
            return this.type.syntaxEquals(otherField.type)
                    && this.identifier.equals(otherField.identifier);
        }
        return false;
    }
}
