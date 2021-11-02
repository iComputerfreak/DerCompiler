package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class IntType extends BasicType {
    
    public IntType() {}

    @Override
    public boolean syntaxEqual(ASTNode other) {
        return super.syntaxEqual(other) && (other instanceof IntType);
    }
}
