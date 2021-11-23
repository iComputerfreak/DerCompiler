package de.dercompiler.semantic;

import de.dercompiler.semantic.type.Type;

public class VariableDefinition implements Definition {

    private final String identifier;
    private final Type type;

    public VariableDefinition(String identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public de.dercompiler.semantic.type.Type getType() {
        return type;
    }
}
