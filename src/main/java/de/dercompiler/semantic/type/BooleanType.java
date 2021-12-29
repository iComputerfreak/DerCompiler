package de.dercompiler.semantic.type;

import de.dercompiler.transformation.FirmTypes;

public class BooleanType implements Type {

    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof BooleanType;
    }

    @Override
    public firm.Type getFirmType() {
        return FirmTypes.booleanFirmType;
    }

    @Override
    public firm.Type getFirmTransformationType() {
        return getFirmType();
    }

    @Override
    public String toString() {
        return "boolean";
    }
}
