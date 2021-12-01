package de.dercompiler.semantic.type;

import de.dercompiler.transformation.FirmTypes;

public class VoidType implements Type {
    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof VoidType;
    }


    @Override
    public firm.Type getFirmType() {
        return FirmTypes.voidFirmType;
    }

    @Override
    public String toString() {
        return "void";
    }
}
