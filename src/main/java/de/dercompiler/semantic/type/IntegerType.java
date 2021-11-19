package de.dercompiler.semantic.type;

public class IntegerType implements Type{

    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof IntegerType;
    }
}
