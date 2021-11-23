package de.dercompiler.semantic.type;

public final class ArrayType implements ReferenceType {
    
    private final Type elementType;

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
    
    /**
     * Returns the dimension of this array type
     */
    public int getDimension() {
        if (elementType instanceof ArrayType a) {
            return a.getDimension() + 1;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return elementType.toString() + "[]";
    }
}
