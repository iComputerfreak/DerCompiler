package de.dercompiler.semantic.type;

/**
 * This class represents an internal class type that may be referenced only in specific cases.
 */
public final class InternalClass extends ClassType {

    public InternalClass(String identifier) {
        super(identifier);
    }
}
