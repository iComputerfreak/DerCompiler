package de.dercompiler.semantic.type;

public class AnyType implements Type {
    @Override
    public boolean isCompatibleTo(Type other) {
        return true;
    }
}
