package de.dercompiler.ast.type;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

/**
 * Represents a type in MiniJava, such as {@code int}, {@code boolean[]}, {@code Foo}, {@code void} or {@code int[][][]}
 */
public final class Type extends ASTNode {

    private final BasicType basicType;
    private final int arrayDimension;

    /**
     * Creates a new Type
     * @param position The source code position
     * @param basicType The underlying basic type
     * @param arrayDimension If this type is an array, the array dimension, otherwise 0
     */
    public Type(SourcePosition position, BasicType basicType, int arrayDimension) {
        super(position);
        this.basicType = basicType;
        this.arrayDimension = arrayDimension;
    }

    /**
     * Returns the underlying basic type
     */
    public BasicType getBasicType() {
        return basicType;
    }

    /**
     * Returns the dimension of this array type or 0, if this type is no array
     */
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
