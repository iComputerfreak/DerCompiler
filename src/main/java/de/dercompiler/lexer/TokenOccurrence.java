package de.dercompiler.lexer;

import de.dercompiler.lexer.token.IToken;

public record TokenOccurrence(IToken type, SourcePosition position) {

    public IToken type() {
        return type;
    }

    @Override
    public SourcePosition position() {
        return position;
    }
}
