package de.dercompiler.ast.type;


import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class BasicType extends ASTNode permits IntType, BooleanType, VoidType, CustomType, ErrorType {
    
    public BasicType(SourcePosition position) {
        super(position);
    }
}
