package de.dercompiler.ast;

import de.dercompiler.lexer.SourcePosition;

public abstract sealed class ClassMember implements ASTNode permits Field, Method, MainMethod {

    protected SourcePosition position;

    public ClassMember(SourcePosition position) {
        this.position = position;
    }

    @Override
    public SourcePosition getSourcePosition() {
        return position;
    }

}
