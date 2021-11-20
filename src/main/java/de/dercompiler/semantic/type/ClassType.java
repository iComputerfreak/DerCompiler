package de.dercompiler.semantic.type;

import de.dercompiler.ast.Field;
import de.dercompiler.ast.Method;

import java.util.HashMap;
import java.util.Map;

public final class ClassType implements ReferenceType {

    private String identifier;
    private Map<String, Field> fieldMap;
    private Map<String, Method> methodMap;

    public ClassType(String identifier) {
        this.identifier = identifier;
        this.fieldMap = new HashMap<>();
        this.methodMap = new HashMap<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isCompatibleTo(Type other) {
        return this == other;
    }

    public Method getMethod(String methodName) {
        return methodMap.getOrDefault(methodName, null);
    }

    public Field getField(String fieldName) {
        return fieldMap.getOrDefault(fieldName, null);
    }

    public boolean hasMethod(String identifier) {
        return methodMap.containsKey(identifier);
    }

    public void addMethod(String identifier, Method method) {
        methodMap.put(identifier, method);
    }

    public boolean hasField(String identifier) {
        return fieldMap.containsKey(identifier);
    }

    public void addField(String identifier, Field field) {
        fieldMap.put(identifier, field);
    }

    @Override
    public String toString() {
        return identifier;
    }
}
