package de.dercompiler.semantic.type;

public class ArrayType implements Type {
    private Type elementType;

    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof ArrayType array && this.elementType.isCompatibleTo(array.elementType);
    }


    public Type getElementType() {
        return elementType;
    }
}
