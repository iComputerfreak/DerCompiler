package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class Type implements ASTNode {

    private final BasicType basicType;
    // INFO: typeRest may be null
    private final int dimension;
    
    public Type(BasicType basicType, int dimension) {
        this.basicType = basicType;
        this.dimension = dimension;
    }

    public BasicType getBasicType() {
        return basicType;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (!(other instanceof Type otherType)) {
            return false;
        }
        if (!this.basicType.syntaxEqual(otherType.basicType)) {
            return false;
        }
        return this.dimension == otherType.dimension;
    }
}
