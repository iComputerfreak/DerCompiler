package de.dercompiler.transformation.node;

public enum NodeAccess {
    UNINITALIZED,
    LOAD,
    ARRAY_ACCESS,
    FIELD_ACCESS,
    METHOD_CALL_BASE;
}
