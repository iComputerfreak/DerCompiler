package de.dercompiler.semantic.type;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.MethodDefinition;
import firm.Entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public sealed class ClassType implements ReferenceType permits InternalClass, DummyClassType {

    private String identifier;
    protected Map<String, FieldDefinition> fieldMap;
    protected Map<String, MethodDefinition> methodMap;

    private ClassDeclaration decl;
    private firm.ClassType firmType;
    private List<firm.Entity> methodEntities;
    private List<firm.Entity> fieldEntities;

    public ClassType(String identifier) {
        this.identifier = identifier;
        this.fieldMap = new HashMap<>();
        this.methodMap = new HashMap<>();
        this.methodEntities = new LinkedList<>();
        this.fieldEntities = new LinkedList<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isCompatibleTo(Type other) {
        return this == other
                || (other instanceof DummyClassType dummy && this.isCompatibleTo(dummy.getRealClass()))
                || other instanceof NullType
                || other instanceof AnyType;
    }
    
    public firm.ClassType getFirmType() {
        return firmType;
    }
    
    public void setFirmType(firm.ClassType firmType) {
        this.firmType = firmType;
    }

    public List<Entity> getMethodEntities() {
        return methodEntities;
    }

    public void setMethodEntities(List<Entity> methodEntities) {
        this.methodEntities = methodEntities;
    }

    public List<Entity> getFieldEntities() {
        return fieldEntities;
    }

    public void setFieldEntities(List<Entity> fieldEntities) {
        this.fieldEntities = fieldEntities;
    }

    public MethodDefinition getMethod(String methodName) {
        return methodMap.getOrDefault(methodName, null);
    }

    public FieldDefinition getField(String fieldName) {
        return fieldMap.getOrDefault(fieldName, null);
    }

    public boolean hasMethod(String identifier) {
        return methodMap.containsKey(identifier);
    }

    public void addMethod(MethodDefinition method) {
        methodMap.put(method.getIdentifier(), method);
    }

    public List<MethodDefinition> getMethods() {
        return List.copyOf(methodMap.values());
    }

    public boolean hasField(String identifier) {
        return fieldMap.containsKey(identifier);
    }

    public void addField(FieldDefinition field) {
        fieldMap.put(field.getIdentifier(), field);
    }

    public List<FieldDefinition> getFields() {
        return List.copyOf(fieldMap.values());
    }

    public ClassDeclaration getDecl() {
        return decl;
    }

    public void setDecl(ClassDeclaration decl) {
        this.decl = decl;
    }

    @Override
    public String toString() {
        return identifier;
    }

}
