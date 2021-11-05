package de.dercompiler.ast;

import de.dercompiler.lexer.SourcePosition;

public abstract sealed class ClassMember extends ASTNode permits Field, Method, MainMethod {

    public ClassMember(SourcePosition position) {
        super(position);
    }

}
