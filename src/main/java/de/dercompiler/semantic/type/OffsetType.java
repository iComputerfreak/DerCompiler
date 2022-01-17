package de.dercompiler.semantic.type;

import de.dercompiler.transformation.FirmTypes;

public class OffsetType implements Type {

    @Override
    public boolean isCompatibleTo(Type other) {
        return other instanceof OffsetType;
    }

    @Override
    public firm.Type getFirmType() {
        return FirmTypes.offsetType;
    }

    @Override
    public firm.Type getFirmTransformationType() {
        return getFirmType();
    }
}
