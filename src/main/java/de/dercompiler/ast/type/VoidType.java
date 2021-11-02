package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class VoidType extends BasicType {
    
    public VoidType() {}

    @Override
    public boolean syntaxEqual(ASTNode other) {
        return super.syntaxEqual(other) && (other instanceof VoidType);
    }
}
