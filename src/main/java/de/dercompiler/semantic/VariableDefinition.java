package de.dercompiler.semantic;

import de.dercompiler.ast.type.Type;

public class VariableDefinition implements Definition {

    private final Symbol symbol;
    private final Type type;

    public VariableDefinition(Symbol symbol, Type type) {
        this.symbol = symbol;
        this.type = type;
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public Type getType() {
        return type;
    }
}
