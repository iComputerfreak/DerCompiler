package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;

public final class TypeRest implements ASTNode {

    // INFO: typeRest may be null
    private final TypeRest typeRest;
    
    public TypeRest(TypeRest typeRest) {
        this.typeRest = typeRest;
    }

    public TypeRest getTypeRest() {
        return typeRest;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        return (other instanceof TypeRest otherTypeRest) 
                && this.typeRest.syntaxEqual(otherTypeRest.typeRest);
    }
}
