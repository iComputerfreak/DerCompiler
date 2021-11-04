package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.SyntaxEquatable;

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
    public boolean syntaxEquals(SyntaxEquatable other) {
        if (other instanceof Type otherType) {
            return this.basicType.syntaxEquals(otherType.basicType)
                    && this.arrayDimension == otherType.arrayDimension; 
        }
        return false;
    }
}
