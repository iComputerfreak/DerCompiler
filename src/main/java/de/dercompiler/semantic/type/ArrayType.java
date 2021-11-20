package de.dercompiler.semantic.type;

import java.lang.reflect.Array;

public final class ArrayType implements ReferenceType {
    private Type elementType;

    public ArrayType(Type elementType) {
        this.elementType = elementType;
    }



    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof ArrayType array && this.elementType.isCompatibleTo(array.elementType)
                || other instanceof NullType || other instanceof AnyType;
    }


    public Type getElementType() {
        return elementType;
    }

    @Override
    public String toString() {
        return elementType.toString() + "[]";
    }
}
