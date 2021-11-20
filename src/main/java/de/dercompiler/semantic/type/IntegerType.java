package de.dercompiler.semantic.type;

public class IntegerType implements Type{

    public IntegerType() {
        
    }

    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof IntegerType || other instanceof AnyType;
    }

    @Override
    public String toString() {
        return "int";
    }
}
