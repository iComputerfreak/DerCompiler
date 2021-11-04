package de.dercompiler.ast.type;

import de.dercompiler.ast.SyntaxEquatable;

public final class BooleanType extends BasicType {
    
    public BooleanType() {}

    @Override
    public boolean syntaxEquals(SyntaxEquatable other) {
        return other instanceof BooleanType;
    }
}
