package de.dercompiler.semantic;

import de.dercompiler.ast.Method;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.MethodType;

public class MethodDefinition implements Definition {

    private final ClassType referenceType;
    private final String identifier;
    private MethodType type;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    private Method method;

    public MethodDefinition(String identifier, MethodType type, ClassType referenceType) {
        this.identifier = identifier;
        this.type = type;
        this.referenceType = referenceType;
    }

    public MethodDefinition(String identifier, ClassType referenceType) {
        this.identifier = identifier;
        this.type = null;
        this.referenceType = referenceType;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public MethodType getType() {
        return type;
    }

    public void setType(MethodType type) {
        this.type = type;
    }
    
    public firm.MethodType getFirmType() {
        return type.getFirmType();
    }
    
    public ClassType getReferenceType() {
        return referenceType;
    }
}
