package de.dercompiler.semantic.type;

public interface Type {

    boolean isCompatibleTo(Type other);
}
