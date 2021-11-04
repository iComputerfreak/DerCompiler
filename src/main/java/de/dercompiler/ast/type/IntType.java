package de.dercompiler.ast.type;

import de.dercompiler.ast.SyntaxEquatable;

public final class IntType extends BasicType {
    
    public IntType() {}

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        return other instanceof IntType;
    }
}
