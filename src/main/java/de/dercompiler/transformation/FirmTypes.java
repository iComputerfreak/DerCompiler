package de.dercompiler.transformation;

import de.dercompiler.semantic.type.BooleanType;
import de.dercompiler.semantic.type.IntegerType;
import de.dercompiler.semantic.type.OffsetType;
import de.dercompiler.semantic.type.VoidType;
import firm.Mode;
import firm.PrimitiveType;

public class FirmTypes {

    // Static global firm types
    public static final firm.Type intFirmType;
    public static final firm.Type offsetType;
    public static final firm.Type booleanFirmType;
    public static final firm.Type voidFirmType;
    public static final firm.Type pointerFirmType;

    static {
        intFirmType = FirmTypeFactory.getInstance()
                .createFirmPrimitiveType(new IntegerType());
        offsetType = FirmTypeFactory.getInstance()
                .createFirmPrimitiveType(new OffsetType());
        booleanFirmType = FirmTypeFactory.getInstance()
                .createFirmPrimitiveType(new BooleanType());
        voidFirmType = FirmTypeFactory.getInstance()
                .createFirmPrimitiveType(new VoidType());
        pointerFirmType = new PrimitiveType(Mode.getP());
    }

}
