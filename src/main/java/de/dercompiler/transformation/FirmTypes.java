package de.dercompiler.transformation;

import de.dercompiler.semantic.type.BooleanType;
import de.dercompiler.semantic.type.IntegerType;
import de.dercompiler.semantic.type.VoidType;

public class FirmTypes {

    // Static global firm types
    public static final firm.Type intFirmType;
    public static final firm.Type booleanFirmType;
    public static final firm.Type voidFirmType;

    static {
        intFirmType = FirmTypeFactory.getInstance()
                .createFirmPrimitiveType(new IntegerType());
        booleanFirmType = FirmTypeFactory.getInstance()
                .createFirmPrimitiveType(new BooleanType());
        voidFirmType = FirmTypeFactory.getInstance()
                .createFirmPrimitiveType(new VoidType());
    }

}
