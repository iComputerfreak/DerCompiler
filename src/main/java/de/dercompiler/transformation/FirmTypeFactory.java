package de.dercompiler.transformation;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.type.*;
import firm.Mode;
import firm.PointerType;
import firm.PrimitiveType;

import java.util.HashMap;

public class FirmTypeFactory {
    
    private static final FirmTypeFactory instance = new FirmTypeFactory();
    
    private final HashMap<String, firm.ArrayType> arrayTypes = new HashMap<>();
    private final HashMap<firm.Type, firm.Type> pointerType = new HashMap<>();
    
    private FirmTypeFactory() {}
    
    public static FirmTypeFactory getInstance() {
        return instance;
    }

    /**
     * Creates a new {@link firm.MethodType} instance with the given properties
     * @param parameterTypes The firm types of all parameters of the function
     * @param returnType The return type of the function, or null, if the function returns void
     * @return The created {@link firm.MethodType}
     */
    public firm.MethodType createFirmMethodType(firm.Type[] parameterTypes, firm.Type returnType) {
        // Void functions have no return types
        if (returnType == null) {
            return new firm.MethodType(parameterTypes, new firm.Type[]{});
        }
        return new firm.MethodType(parameterTypes, new firm.Type[]{returnType});
    }

    /**
     * Creates a new {@link firm.ClassType} instance from the given {@link ClassType}
     * @param classType The class to create the firm type for
     * @return The created {@link firm.ClassType}
     */
    public firm.ClassType createFirmClassType(ClassType classType) {
        return new firm.ClassType(classType.getMangledIdentifier());
    }

    public firm.Type createOrGetFirmPointerType(firm.Type type) {
        if (pointerType.containsKey(type)) {
            return pointerType.get(type);
        }
        firm.Type pointer = new PointerType(type);
        pointerType.put(type, pointer);
        return pointer;
    }
    
    /**
     * Creates a new {@link firm.PrimitiveType} instance from the given primitive type
     * @param type The type to create a firm type for. May only be an IntegerType, BooleanType, VoidType or NullType,
     *             otherwise the function throws an internal error
     * @return The created {@link firm.PrimitiveType}
     */
    public firm.Type createFirmPrimitiveType(Type type) {
        if (type instanceof IntegerType) {
            return new PrimitiveType(Mode.getIs());
        } else if (type instanceof OffsetType) {
            return new PrimitiveType(Mode.getLs());
        } else if (type instanceof BooleanType) {
            // For booleans, we use the byte type (signed or unsigned should not matter)
            return new PrimitiveType(Mode.getBu());
        } else if (type instanceof VoidType) {
            return new PrimitiveType(Mode.getANY());
        } else if (type instanceof NullType t) {
            return getOrCreateFirmVariableType(t.getExpectedType());
        } else {
            // If we reach this, we requested a firm type for a semantic type that is not primitive
            new OutputMessageHandler(MessageOrigin.TRANSFORM)
                    .internalError("Requested a primitive firm type for a type that is not primitive.");
            throw new RuntimeException();
        }
    }

    /**
     * Returns the firm.Type for the given semantic type.
     * The given type has to be either a primitive type (IntegerType, BooleanType, VoidType or NullType)
     * or a ClassType.
     * If the firm.Type for the given semantic type does not yet exist, it is created.
     * @param type The semantic type to return the firm.Type for
     * @return The firm.Type for the given semantic type or null, if the given type does not match the
     * requirements described above.
     */
    public firm.Type getOrCreateFirmVariableType(Type type) {
        if (type instanceof IntegerType) {
            return FirmTypes.intFirmType;
        } else if (type instanceof BooleanType) {
            return FirmTypes.booleanFirmType;
        } else if (type instanceof VoidType) {
            return FirmTypes.voidFirmType;
        } else if (type instanceof NullType nullType) {
            return getOrCreateFirmVariableType(nullType.getExpectedType());
        } else if (type instanceof ClassType t) {
            // Check if the firm type was already set, otherwise create and set it now
            if (t.getFirmType() == null) {
                t.setFirmType(createFirmClassType(t));
            }
            return t.getFirmTransformationType();
        } else if (type instanceof ArrayType t) {
            firm.Type elementFirmType = getOrCreateFirmVariableType(t.getElementType());
            return createOrGetFirmPointerType(elementFirmType);
        }
        return null;
    }
    
}
