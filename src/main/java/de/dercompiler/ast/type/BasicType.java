package de.dercompiler.ast.type;


import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class BasicType extends ASTNode permits IntType, BooleanType, VoidType, CustomType {
    
    public BasicType(SourcePosition position) {
        super(position);
    }

    @Override
    public abstract String toString();
}
