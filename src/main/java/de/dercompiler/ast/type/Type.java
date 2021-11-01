package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class Type implements ASTNode {
    // INFO: typeRest may be null
    public Type(BasicType basicType, TypeRest typeRest) {}

    @Override
    public boolean syntaxEqual(ASTNode other) {
        // TODO implement
        return true;
    }
}
