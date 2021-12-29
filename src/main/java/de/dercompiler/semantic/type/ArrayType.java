package de.dercompiler.semantic.type;

import de.dercompiler.transformation.FirmTypes;

public final class ArrayType implements ReferenceType {
    
    private final Type baseType;
    private final int dimension;

    public ArrayType(Type baseType) {
        this(baseType, 1);
    }

    public ArrayType(Type baseType, int dimension) {
        assert(dimension > 0);
        int base = 0;
        if (baseType instanceof ArrayType at) {
            this.baseType = at.getElementType();
            base = at.dimension;
        } else {
            this.baseType = baseType;
        }
        this.dimension = base + dimension;
    }
    
    @Override
    public boolean isCompatibleTo(Type other) {
        if (other instanceof NullType nullType) {
            nullType.setExpectedType(this);
        }

        return (other instanceof ArrayType array && this.baseType.isCompatibleTo(array.baseType) && this.dimension == array.dimension)
                || other instanceof NullType;
    }
    
    public Type getElementType() {
        if (dimension == 1) return baseType;
        return new ArrayType(baseType, dimension - 1);
    }

    @Override
    public firm.Type getFirmType() {
        return baseType.getFirmType();
    }

    @Override
    public firm.Type getFirmTransformationType() {
        return FirmTypes.pointerFirmType;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public String toString() {
        return getElementType().toString() + "[]";
    }
}
