package de.dercompiler.transformation;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.type.*;
import firm.Mode;
import firm.PrimitiveType;

import java.util.HashMap;

public class FirmTypeFactory {
    
    private static final FirmTypeFactory instance = new FirmTypeFactory();
    
    private final HashMap<String, firm.ArrayType> arrayTypes = new HashMap<>();
    
    private FirmTypeFactory() {}
    
    public static FirmTypeFactory getInstance() {
        return instance;
    }
    
    public firm.MethodType createFirmMethodType(firm.Type[] parameterTypes, firm.Type returnType) {
        // Void functions have no return types
        if (returnType == null) {
            return new firm.MethodType(parameterTypes, new firm.Type[]{});
        }
        return new firm.MethodType(parameterTypes, new firm.Type[]{returnType});
    }
    
    public firm.ClassType createFirmClassType(ClassType classType) {
        return new firm.ClassType(classType.getIdentifier());
    }
    
    /**
     *
     * @param type
     * @return
     */
    public firm.PrimitiveType createFirmPrimitiveType(Type type) {
        if (type instanceof IntegerType) {
            return new PrimitiveType(Mode.getIs());
        } else if (type instanceof BooleanType) {
            // For booleans, we use the byte type (signed or unsigned should not matter)
            return new PrimitiveType(Mode.getBs());
        } else if (type instanceof VoidType) {
            return null;
        } else if (type instanceof NullType t) {
            // TODO: What type for null?
            // return getOrCreateFirmVariableType(t.getExpectedType())
            return null;
        } else {
            // If we reach this, we requested a firm type for a semantic type that is not primitive
            new OutputMessageHandler(MessageOrigin.TRANSFORM)
                    .internalError("Requested a primitive firm type for a type that is not primitive.");
            throw new RuntimeException();
        }
    }
    
    private firm.ArrayType getOrCreateFirmArrayType(Type elementType, firm.Type elementFirmType, int numberOfElements) {
        // Get the HashMap for the element type
        String identifier = getTypeIdentifier(elementType);
        if (!arrayTypes.containsKey(identifier)) {
            firm.ArrayType firmType = new firm.ArrayType(elementFirmType, numberOfElements);
            arrayTypes.put(identifier, firmType);
        }
        return arrayTypes.get(identifier);
    }
    
    private String getTypeIdentifier(Type type) {
        if (type instanceof ClassType t) {
            return t.getIdentifier();
        } else if (type instanceof IntegerType) {
            return "int";
        } else if (type instanceof BooleanType) {
            return "boolean";
        } else if (type instanceof VoidType) {
            return "void";
        } else if (type instanceof NullType) {
            return "null";
        } else {
            new OutputMessageHandler(MessageOrigin.TRANSFORM)
                    .internalError("Type identifier for type \" + type + \" could not be computed.");
            throw new RuntimeException();
        }
    }
    
    /**
     * Returns the firm.Type for the given semantic type.
     * The given type has to be a either a primitive type (IntegerType, BooleanType, VoidType or NullType)
     * or a ClassType.
     * If the firm.Type for the given semantic type does not yet exist, it is created.
     * @param type The semantic type to return the firm.Type for
     * @return The firm.Type for the given semantic type or null, if the given type does not match the
     * requirements described above.
     */
    public firm.Type getOrCreateFirmVariableType(Type type) {
        if (type instanceof IntegerType) {
            return GlobalScope.intFirmType;
        } else if (type instanceof BooleanType) {
            return GlobalScope.booleanFirmType;
        } else if (type instanceof VoidType) {
            return GlobalScope.voidFirmType;
        } else if (type instanceof NullType) {
            return GlobalScope.nullFirmType;
        } else if (type instanceof ClassType t) {
            // Check if the firm type was already set, otherwise create and set it now
            if (t.getFirmType() == null) {
                t.setFirmType(createFirmClassType(t));
            }
            return t.getFirmType();
        } else if (type instanceof ArrayType t) {
            firm.Type elementFirmType = getOrCreateFirmVariableType(t.getElementType());
            // TODO: Where to get?
            int numberOfElements = 0;
            return getOrCreateFirmArrayType(t.getElementType(), elementFirmType, numberOfElements);
        }
        return null;
    }
    
}
