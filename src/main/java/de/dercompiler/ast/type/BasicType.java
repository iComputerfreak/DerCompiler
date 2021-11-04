package de.dercompiler.ast.type;


import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

public abstract sealed class BasicType implements ASTNode permits IntType, BooleanType, VoidType, CustomType {

    private final SourcePosition position;

    public BasicType(SourcePosition position) {
        this.position = position;
    }

    @Override
    public SourcePosition getSourcePosition() {
        return position;
    }
}
