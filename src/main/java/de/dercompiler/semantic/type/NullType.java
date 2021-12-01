package de.dercompiler.semantic.type;

public class NullType implements Type {

    private Type expectedType;

    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof ReferenceType || other instanceof NullType;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public firm.Type getFirmType() {
        return getExpectedType().getFirmType();
    }

    public void setExpectedType(Type expType) {
        this.expectedType = expType;
    }

    public Type getExpectedType() {
        return expectedType;
    }
}
