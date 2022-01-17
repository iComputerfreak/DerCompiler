package de.dercompiler.semantic.type;

import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.MethodDefinition;

public final class DummyClassType extends ClassType {

    private ClassType realType;
    public DummyClassType(String identifier) {
        super(identifier);
    }

    public void setRealType(ClassType realType) {
        this.realType = realType;
    }

    @Override
    public boolean isCompatibleTo(Type other) {
        return realType != null && realType.isCompatibleTo(other);
    }

    @Override
    public boolean hasMethod(String identifier) {
        return realType.hasMethod(identifier);
    }

    @Override
    public MethodDefinition getMethod(String methodName) {
        return realType.getMethod(methodName);
    }

    @Override
    public boolean hasField(String identifier) {
        return realType.hasField(identifier);
    }

    @Override
    public FieldDefinition getField(String fieldName) {
        return realType.getField(fieldName);
    }

    public Type getRealClass() {
        return realType;
    }

    public boolean hasRealClass() {
        return realType != null;
    }
}
