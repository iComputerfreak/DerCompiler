package de.dercompiler.semantic.type;

public class NullType implements Type {

    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof ReferenceType || other instanceof NullType;
    }

    @Override
    public String toString() {
        return "null";
    }
}
