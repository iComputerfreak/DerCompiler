package de.dercompiler.semantic.type;

public class VoidType implements Type {
    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof VoidType;
    }

    @Override
    public String toString() {
        return "void";
    }
}
