package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class Type implements ASTNode {

    private final BasicType basicType;
    // INFO: typeRest may be null
    private final int arrayDimension;
    
    public Type(BasicType basicType, int arrayDimension) {
        this.basicType = basicType;
        this.arrayDimension = arrayDimension;
    }

    public BasicType getBasicType() {
        return basicType;
    }

    public int getArrayDimension() {
        return arrayDimension;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (!(other instanceof Type otherType)) {
            return false;
        }
        if (!this.basicType.syntaxEqual(otherType.basicType)) {
            return false;
        }
        return this.arrayDimension == otherType.arrayDimension;
    }
}
