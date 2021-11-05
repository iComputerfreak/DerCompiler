package de.dercompiler.ast;

import de.dercompiler.lexer.SourcePosition;

public final class ErrorClassMember extends ClassMember {

    public ErrorClassMember(SourcePosition position) {
        super(position);
    }
}
