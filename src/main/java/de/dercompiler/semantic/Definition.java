package de.dercompiler.semantic;

import de.dercompiler.ast.type.Type;

/**
 * Represents a definition of a symbol with a given type
 */
public interface Definition {
    Symbol getSymbol();
    Type getType();
}
