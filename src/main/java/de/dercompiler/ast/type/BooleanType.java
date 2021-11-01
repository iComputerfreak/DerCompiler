package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.type.BasicType;

public final class BooleanType extends BasicType {
    public BooleanType() {}

    @Override
    public boolean syntaxEqual(ASTNode other) {
        // TODO implement
        return false;
    }
}
