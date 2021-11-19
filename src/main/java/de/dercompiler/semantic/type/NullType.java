package de.dercompiler.semantic.type;

public class NullType implements Type {

    @Override
    public boolean isCompatibleTo(Type other) {
        return true;
    }
}
