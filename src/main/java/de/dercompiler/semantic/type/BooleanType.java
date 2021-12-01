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
    public String toString() {
        return "boolean";
    }
}
