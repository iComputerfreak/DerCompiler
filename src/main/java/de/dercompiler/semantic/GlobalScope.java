package de.dercompiler.semantic;

import de.dercompiler.semantic.type.*;
import de.dercompiler.transformation.FirmTypeFactory;

import java.util.HashMap;
import java.util.Map;

public class GlobalScope {
    
    private final Map<String, ClassType> classMap;
    
    // Static global firm types
    public static final firm.PrimitiveType intFirmType = FirmTypeFactory.getInstance()
            .createFirmPrimitiveType(new IntegerType());
    public static final firm.PrimitiveType booleanFirmType = FirmTypeFactory.getInstance()
            .createFirmPrimitiveType(new BooleanType());
    public static final firm.PrimitiveType voidFirmType = FirmTypeFactory.getInstance()
            .createFirmPrimitiveType(new VoidType());
    public static final firm.PrimitiveType nullFirmType = FirmTypeFactory.getInstance()
            .createFirmPrimitiveType(new NullType());

    public GlobalScope() {
        this.classMap = new HashMap<>();
    }

    public MethodDefinition getMethod(String className, String methodName) {
        return getClass(className).getMethod(methodName);
    }

    public FieldDefinition getField(String className, String fieldName) {
        return getClass(className).getField(fieldName);
    }

    public ClassType getClass(String className) {
        return classMap.get(className);
    }

    public boolean hasClass(String identifier) {
        return classMap.containsKey(identifier);
    }

    public void addClass(ClassType newClass) {
        classMap.put(newClass.getIdentifier(), newClass);
    }
    
}
