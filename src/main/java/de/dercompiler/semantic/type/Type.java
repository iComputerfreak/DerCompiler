package de.dercompiler.semantic.type;

public interface Type {

    boolean isCompatibleTo(Type other);

    firm.Type getFirmType();

    firm.Type getFirmTransformationType();
}
