package de.dercompiler.semantic;

import de.dercompiler.ast.type.Type;

public class MethodDefinition implements Definition {
    
    private final Symbol symbol;
    private final Type type;

    public MethodDefinition(Symbol symbol, Type type) {
        this.symbol = symbol;
        this.type = type;
    }

    @Override
    public Symbol getSymbol() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }
}
