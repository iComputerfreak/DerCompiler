package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class CustomType extends BasicType {

    public CustomType(String identifier) {}

    @Override
    public boolean syntaxEqual(ASTNode other) {
        // TODO implement
        return true;
    }
}
