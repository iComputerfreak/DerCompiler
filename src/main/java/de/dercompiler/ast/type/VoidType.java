package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class VoidType extends BasicType {
    
    public VoidType() {}

    @Override
    public boolean syntaxEquals(ASTNode other) {
        return other instanceof VoidType;
    }
}
