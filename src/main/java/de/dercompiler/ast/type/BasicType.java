package de.dercompiler.ast.type;


import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.lexer.SourcePosition;

/**
 * Represents a basic type in MiniJava, such as {@code void}, {@code int}, {@code boolean} or any custom type
 */
public abstract sealed class BasicType extends ASTNode permits IntType, BooleanType, VoidType, CustomType, ErrorType {

    /**
     * Creates a new BasicType
     * @param position The source code position
     */
    public BasicType(SourcePosition position) {
        super(position);
    }
    
    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitBasicType(this);
    }
    
}
