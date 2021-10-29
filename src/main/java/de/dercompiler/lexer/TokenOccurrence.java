package de.dercompiler.lexer;

import de.dercompiler.lexer.token.IToken;

public record TokenOccurrence(IToken type, Lexer.Position position) {

    public IToken type() {
        return type;
    }

    @Override
    public Lexer.Position position() {
        return position;
    }
}
