package de.dercompiler.ast.type;

import de.dercompiler.ast.SyntaxEquatable;

public final class VoidType extends BasicType {
    
    public VoidType() {}

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        return other instanceof VoidType;
    }
}
