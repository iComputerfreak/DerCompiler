package de.dercompiler.semantic.type;

import de.dercompiler.transformation.FirmTypes;

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

    @Override
    public firm.Type getFirmTransformationType() {
        return FirmTypes.pointerFirmType;
    }

    public void setExpectedType(Type expType) {
        this.expectedType = expType;
    }

    public Type getExpectedType() {
        return expectedType;
    }
}
