package de.dercompiler.transformation;

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
        return new firm.MethodType(parameterTypes, new firm.Type[] {returnType});
    }
    
    public firm.ClassType createFirmClassType(ClassType classType) {
        return new firm.ClassType(classType.getIdentifier());
    }
    
    public firm.PrimitiveType createFirmPrimitiveType(Type type) {
        if (type instanceof IntegerType) {
            return new PrimitiveType(Mode.getIs());
        } else if (type instanceof BooleanType) {
            // For booleans, we use the byte type (signed or unsigned should not matter)
            return new PrimitiveType(Mode.getBs());
        } else if (type instanceof VoidType) {
            // TODO: How to represent void?
            return null;
        } else if (type instanceof NullType) {
            // TODO: How to represent null?
            return null;
        } else {
            // If we reach this, we requested a firm type for a semantic type that is not primitive
            throw new RuntimeException("Requested a primitive firm type for a type that is not primitive.");
        }
    }
    
    public firm.ArrayType getOrCreateFirmArrayType(Type elementType, firm.Type elementFirmType, int numberOfElements) {
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
            throw new RuntimeException("Type identifier for type " + type + " could not be computed.");
        }
    }
    
}
