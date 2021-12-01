package de.dercompiler.transformation;

import de.dercompiler.semantic.type.BooleanType;
import de.dercompiler.semantic.type.IntegerType;
import de.dercompiler.semantic.type.VoidType;

public class FirmTypes {

    // Static global firm types
    public static final firm.Type intFirmType = FirmTypeFactory.getInstance()
            .createFirmPrimitiveType(new IntegerType());
    public static final firm.Type booleanFirmType = FirmTypeFactory.getInstance()
            .createFirmPrimitiveType(new BooleanType());
    public static final firm.Type voidFirmType = FirmTypeFactory.getInstance()
            .createFirmPrimitiveType(new VoidType());

}
