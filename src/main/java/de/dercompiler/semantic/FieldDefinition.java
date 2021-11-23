package de.dercompiler.semantic;

import de.dercompiler.ast.Field;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.Type;

public class FieldDefinition implements Definition {

    private final ClassType referenceType;
    private final String identifier;
    private Type type;
    private Field node;

    public FieldDefinition(String identifier, Type type, ClassType referenceType){
        this.identifier = identifier;
        this.type = type;
        this.referenceType = referenceType;
    }

    public FieldDefinition(String identifier, ClassType referenceType){
        this.identifier = identifier;
        this.type = null;
        this.referenceType = referenceType;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    public ClassType getReferenceType() {
        return referenceType;
    }

    public Field getNode() {
        return this.node;
    }

    public void setNode(Field node) {
        this.node = node;
    }
}
