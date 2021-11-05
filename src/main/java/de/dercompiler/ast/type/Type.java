package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

public final class Type extends ASTNode {

    private final BasicType basicType;
    // INFO: typeRest may be null
    private final int arrayDimension;
    
    public Type(SourcePosition position, BasicType basicType, int arrayDimension) {
        super(position);
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
    public boolean syntaxEquals(ASTNode other) {
        if (other instanceof Type otherType) {
            return this.basicType.syntaxEquals(otherType.basicType)
                    && this.arrayDimension == otherType.arrayDimension; 
        }
        return false;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitType(this);
    }
}
