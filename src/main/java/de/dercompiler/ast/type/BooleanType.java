package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class BooleanType extends BasicType {
    
    public BooleanType() {}

    @Override
    public boolean syntaxEquals(ASTNode other) {
        return other instanceof BooleanType;
    }
}
