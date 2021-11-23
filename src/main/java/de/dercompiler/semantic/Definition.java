package de.dercompiler.semantic;

import de.dercompiler.semantic.type.Type;

/**
 * Represents a definition of a symbol with a given type
 */
public interface Definition {
    String getIdentifier();
    Type getType();
}
