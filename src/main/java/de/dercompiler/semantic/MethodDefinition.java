package de.dercompiler.semantic;

import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.MethodType;

public class MethodDefinition implements Definition {

    private final ClassType referenceType;
    private final String identifier;
    private firm.MethodType firmType;
    private MethodType type;

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
        return firmType;
    }
    
    public void setFirmType(firm.MethodType firmType) {
        this.firmType = firmType;
    }
    
    public ClassType getReferenceType() {
        return referenceType;
    }
}
