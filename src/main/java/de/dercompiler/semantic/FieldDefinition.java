package de.dercompiler.semantic;

import de.dercompiler.ast.type.Type;

public class FieldDefinition implements Definition{

    private Symbol symbol;
    private Type type;

    public FieldDefinition(Symbol symbol, Type type){
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
