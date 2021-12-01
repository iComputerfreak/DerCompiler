package de.dercompiler.semantic.type;

import de.dercompiler.transformation.FirmTypes;

public class IntegerType implements Type{

    public IntegerType() {
        
    }

    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof IntegerType;
    }

    @Override
    public firm.Type getFirmType() {
        return FirmTypes.intFirmType;
    }

    @Override
    public String toString() {
        return "int";
    }
}
