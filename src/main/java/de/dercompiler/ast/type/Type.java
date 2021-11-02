package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class Type implements ASTNode {

    private final BasicType basicType;
    // INFO: typeRest may be null
    private final TypeRest typeRest;
    
    public Type(BasicType basicType, TypeRest typeRest) {
        this.basicType = basicType;
        this.typeRest = typeRest;
    }

    public BasicType getBasicType() {
        return basicType;
    }

    public TypeRest getTypeRest() {
        return typeRest;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (!(other instanceof Type otherType)) {
            return false;
        }
        if (!this.basicType.syntaxEqual(otherType.basicType)) {
            return false;
        }
        return (this.typeRest == null && otherType.typeRest == null) 
                || this.typeRest.syntaxEqual(otherType.typeRest);
    }
}
