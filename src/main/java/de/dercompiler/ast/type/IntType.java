package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class IntType extends BasicType {
    
    public IntType() {}

    @Override
    public boolean syntaxEquals(ASTNode other) {
        return other instanceof IntType;
    }
}
