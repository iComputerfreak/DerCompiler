package de.dercompiler.semantic.type;

public class BooleanType implements Type {

    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof BooleanType;
    }

    @Override
    public String toString() {
        return "boolean";
    }
}
