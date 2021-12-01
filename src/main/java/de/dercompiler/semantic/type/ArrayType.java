package de.dercompiler.semantic.type;

public final class ArrayType implements ReferenceType {
    
    private final Type elementType;

    public ArrayType(Type elementType) {
        this.elementType = elementType;
    }
    
    @Override
    public boolean isCompatibleTo(Type other) {
        if (other instanceof NullType nullType) {
            nullType.setExpectedType(this);
        }

        return other instanceof ArrayType array && this.elementType.isCompatibleTo(array.elementType)
                || other instanceof NullType;
    }
    
    public Type getElementType() {
        return elementType;
    }

    @Override
    public firm.Type getFirmType() {
        return null;
    }

    @Override
    public String toString() {
        return elementType.toString() + "[]";
    }
}
